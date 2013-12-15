package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;

public class Player {

	private Row playerRow;

	private String playerName;
	private Party playerParty;
	private int playerLevel;
	private double playerExperience;
	private EnumFactions faction;

	public boolean isClient;

	@SideOnly(Side.CLIENT)
	private static Map<String, Player> playersClient = new HashMap<String, Player>();

	private static Map<String, Player> playersServer = new HashMap<String, Player>();

	public static Player getPlayer(String username, boolean isClient) {
		Map<String, Player> players = isClient ? playersClient : playersServer;
		return players.containsKey(username) ? players.get(username) : new Player(username, isClient);
	}

	private Player(String name, boolean isClient) {
		playerName = name;
		Map<String, Player> players = isClient ? playersClient : playersServer;
		players.put(name, this);
		Database db = RPGThing.getProxy().getDatabase();
		Table playerTable = db.get("players");
		try {
			playerRow = playerTable.match(playerTable.map("name"), playerName, 1).get(0);
			unpack();
		} catch (IndexOutOfBoundsException oob) {
			int r = playerTable.put(new Object[] { playerTable.rows(), playerName, 0, 1, 0.0d, -1 });
			playerRow = playerTable.get(r);
		}
		this.isClient = isClient;
	}

	public String getName() {
		return playerName;
	}

	public void setParty(Party party) {
		playerParty = party;
		commit();
	}

	public Party getParty() {
		return playerParty;
	}

	public int getLevel() {
		return playerLevel;
	}

	public void setLevel(int l) {
		playerLevel = l;
		commit();
	}

	public double getExperience() {
		return playerExperience;
	}

	public void setExperience(double e) {
		playerExperience = e;
		commit();
	}

	private void commit() {
		playerRow.put(2, faction.ordinal());
		playerRow.put(3, playerLevel);
		playerRow.put(4, playerExperience);
		if (playerParty == null)
			playerRow.put(5, -1);
		else
			playerRow.put(5, playerParty.getId());

		if (!isClient) {
			StandardModPacket packet = new StandardModPacket();
			packet.setIsForServer(false);
			packet.setType("player");
			packet.setValue("payload", "player-data");
			packet.setValue("player-name", playerName);
			writeToPacket(packet, "player-data");
			RPGThing.getProxy().sendToAllPlayers(packet);
		}
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

}
