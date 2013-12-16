package uk.co.cloudhunter.rpgthing.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.StepSound;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;
import uk.co.cloudhunter.rpgthing.partyline.PartyInvite;

public class Player {

	public static ReentrantLock playerLock = new ReentrantLock();
	private static Map<String, Player> playersClient = new HashMap<String, Player>();
	private static Map<String, Player> playersServer = new HashMap<String, Player>();

	private Row playerRow;

	private String playerName;
	private Party playerParty;
	private int playerLevel;
	private double playerExperience;
	private int playerUnspentSkillPoints;
	private EnumFactions faction = EnumFactions.OVERWORLD;

	private WeakReference<EntityPlayer> weakPlayer = new WeakReference<EntityPlayer>(null);
	private WeakReference<EntityPlayer> weakClientPlayer = new WeakReference<EntityPlayer>(null);

	public Map<String, PartyInvite> partyInvites = new HashMap<String, PartyInvite>();

	public boolean isClient;
	public boolean isModified;

	public boolean hasDisconnected = false;

	public static Player getPlayer(String username, boolean isClient) {
		Map<String, Player> players = isClient ? playersClient : playersServer;
		try {
			playerLock.lock();
			if (players.containsKey(username)) {
				Player p = players.get(username);
				playerLock.unlock();
				return p;
			} else {
				playerLock.unlock();
				return new Player(username, isClient);
			}
		} finally {
			if (playerLock.isHeldByCurrentThread())
				playerLock.unlock();
		}
	}

	public static Player[] getAllPlayers(boolean isClient) {
		Map<String, Player> players = isClient ? playersClient : playersServer;
		playerLock.lock();
		Player[] result = players.values().toArray(new Player[0]);
		playerLock.unlock();
		return result;
	}

	public static void removePlayer(Player player) {
		playerLock.lock();
		if (playersServer.containsValue(player)) {
			RPGThing.getLog().info("Removing player " + player.getName() + " from cache");
			playersServer.remove(player);
		}
		playerLock.unlock();
	}

	private Player(String name, boolean isClient) {
		playerName = name;
		Map<String, Player> players = isClient ? playersClient : playersServer;
		playerLock.lock();
		players.put(name, this);
		playerLock.unlock();
		Database db = RPGThing.getProxy().getDatabase();
		Table playerTable = db.get("players");
		try {
			playerRow = playerTable.match(playerTable.map("name"), playerName, 1).get(0);
			unpack();
		} catch (IndexOutOfBoundsException oob) {
			int r = playerTable.put(new Object[] { playerTable.rows(), playerName, 0, 1, 0.0d, -1, 0 });
			playerRow = playerTable.get(r);
		}
		this.isClient = isClient;
	}

	public String getName() {
		return playerName;
	}

	public void setParty(Party party) {
		playerParty = party;
		isModified = true;
		commit();
	}

	public void inviteToParty(Party party) {
		partyInvites.put(party.getOwner().getName(), new PartyInvite(party, this));
		getMinecraftPlayer().sendChatToPlayer(
				ChatMessageComponent.createFromTranslationWithSubstitutions("commands.party.invited", party.getOwner()
						.getName()));
	}

	public Party getParty() {
		return playerParty;
	}

	public int getLevel() {
		return playerLevel;
	}

	public void setLevel(int l) {
		if (l > playerLevel) {
			int diff = l - playerLevel;
			playerUnspentSkillPoints += diff;
		}
		playerLevel = l;
		isModified = true;
		commit();
	}

	public double getExperience() {
		return playerExperience;
	}

	public void setExperience(double e) {
		playerExperience = e;
		isModified = true;
		commit();
	}

	public void addExperience(double quantity) {
		double effectiveLevel = getLevel() + (0.01 * getExperience());
		double factorExperienceEffectiveness = 0.5 + 1 / (0.125 * Math.pow(effectiveLevel, 2) + 2);
		RPGThing.getLog().info("Adding" + (factorExperienceEffectiveness * quantity) + " XP");
		setExperience(getExperience() + factorExperienceEffectiveness * quantity);
		calculateLevels();
	}

	private void calculateLevels() {
		while (getExperience() >= 100.0d) {
			double quantity = getExperience();
			setExperience(quantity - 100.0d);
			setLevel(getLevel() + 1);
		}
	}

	public int getUnspentSkillPoints() {
		return playerUnspentSkillPoints;
	}

	private void commit() {
		playerRow.put(2, faction.ordinal());
		playerRow.put(3, playerLevel);
		playerRow.put(4, playerExperience);
		if (playerParty == null)
			playerRow.put(5, -1);
		else
			playerRow.put(5, playerParty.getId());
		playerRow.put(6, playerUnspentSkillPoints);
	}

	private void unpack() {
		faction = EnumFactions.fromOrdinal((Integer) playerRow.get(2));
		playerLevel = (Integer) playerRow.get(3);
		playerExperience = (Double) playerRow.get(4);
		int partyId = (Integer) playerRow.get(5);
		if (partyId == -1)
			playerParty = null;
		else
			playerParty = Party.getPartyById(partyId, isClient);
		playerUnspentSkillPoints = (Integer) playerRow.get(6);
	}

	public void writeToPacket(StandardModPacket packet, String node) {
		packet.setValue(node, playerRow.values());
	}

	public void readFromPacket(StandardModPacket packet, String node) {
		HashMap<Integer, Object> vals = (HashMap<Integer, Object>) packet.getValue(node);
		for (Entry<Integer, Object> val : vals.entrySet())
			playerRow.put(val.getKey(), val.getValue());
		unpack();
	}

	public int getId() {
		return (Integer) playerRow.get(0);
	}

	public boolean isThisPlayer(EntityPlayer entity) {
		return entity.username.equals(playerName);
	}

	@SideOnly(Side.CLIENT)
	public EntityPlayer getClientPlayer() {
		EntityPlayer player = weakClientPlayer.get();
		if (player == null) {
			List<EntityPlayer> worldPlayers = Minecraft.getMinecraft().theWorld.playerEntities;

			for (EntityPlayer worldPlayer : worldPlayers)
				if (worldPlayer.username.equals(playerName)) {
					player = worldPlayer;
					break;
				}

			weakClientPlayer = new WeakReference(player);
		}

		return player;
	}

	public EntityPlayer getMinecraftPlayer() {
		EntityPlayer player = weakPlayer.get();
		if (player == null) {
			player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
					.getPlayerForUsername(playerName);
			weakPlayer = new WeakReference(player);
		}

		return player;
	}

	public boolean pollModified() {
		if (isModified) {
			isModified = false;
			return true;
		}
		return false;
	}

	public void clearInvites() {
		Iterator<PartyInvite> it = partyInvites.values().iterator();
		while (it.hasNext())
			it.next().declineNoRemove();
		partyInvites.clear();
	}

	public void removeInvite(String ownerName) {
		partyInvites.remove(ownerName);
	}

	public boolean isDisconnected() {
		return hasDisconnected;
	}

}
