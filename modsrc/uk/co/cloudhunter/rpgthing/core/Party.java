package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;

public class Party {

	private Row partyRow;
	private ArrayList<Player> players = new ArrayList<Player>();
	private Player owner;
	@SideOnly(Side.CLIENT)
	private static Map<Integer, Party> partiesClient = new HashMap<Integer, Party>();

	private static Map<Integer, Party> partiesServer = new HashMap<Integer, Party>();

	public boolean isClient;
	
	private boolean isModified;

	public static Party getPartyById(int id, boolean isClient) {
		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		return parties.containsKey(id) ? parties.get(id) : new Party(id, isClient);
	}

	public static Party newParty(boolean isClient) {
		return new Party(isClient);
	}

	private Party(boolean isClient) {
		this.isClient = isClient;
		Database db = RPGThing.getProxy().getDatabase();
		Table partyTable = db.get("parties");
		int r = partyTable.put(new Object[] { partyTable.rows(), "" });
		partyRow = partyTable.get(r);

		unpack();

		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		parties.put(this.getId(), this);
		isModified = true;
	}

	private Party(int id, boolean isClient) {
		this.isClient = isClient;
		Database db = RPGThing.getProxy().getDatabase();
		Table partyTable = db.get("parties");
		try {
			partyRow = partyTable.match(partyTable.map("uid"), id, 1).get(0);
		} catch (IndexOutOfBoundsException exc) {
			int r = partyTable.put(new Object[] { id, "" });
			partyRow = partyTable.get(r);
		}

		unpack();

		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		parties.put(this.getId(), this);
		
		isModified = true;
	}

	public void addPlayer(Player p) {
		synchronized (players) {
			if (!players.contains(p))
				players.add(p);
			p.setParty(this);
		}
		commit();
		isModified = true; 
	}

	public void removePlayer(Player p) {
		synchronized (players) {
			players.remove(p);
		}
		commit();
		isModified = true;
	}

	public Player[] getPlayers() {
		return players.toArray(new Player[0]);
	}

	public void setOwner(Player p) {
		this.owner = p;
		isModified = true;
	}

	public Player getOwner() {
		return owner;
	}

	public int getId() {
		return (Integer) partyRow.get(0);
	}

	public void disband() {
		RPGThing.getProxy().getDatabase().get("parties").remove(partyRow.id());
		isModified = true;
	}

	private void unpack() {
		String members = (String) partyRow.get(1);
		if (members != "") {
			players = new ArrayList<Player>();
			for (String str : members.split(","))
				players.add(Player.getPlayer(str, isClient));
		}
		isModified = true;

	}

	private void commit() {
		StringBuilder members = new StringBuilder();
		synchronized (players) {
			for (Player p : players)
				members.append(p.getId()).append(",");
		}
		partyRow.put(1, members.toString());

		if (!isClient) {
			StandardModPacket result = new StandardModPacket();
			result.setIsForServer(false);
			result.setType("partyline");
			writeToPacket(result, "party-data");
			result.setValue("payload", "party-data");
			result.setValue("party-target", getId());
			List<EntityPlayer> players = MinecraftServer.getServer().getServerConfigurationManager(
					MinecraftServer.getServer()).playerEntityList;
			for (EntityPlayer player : players)
				for (Player p : this.players)
					if (p.getName().equals(player.username))
						RPGThing.getProxy().sendToPlayer((EntityPlayer) player, result);

		}
	}

	public void writeToPacket(StandardModPacket packet, String node) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		HashMap<Integer, String> players = new HashMap<Integer, String>();
		for (Player p : getPlayers())
			players.put(players.size(), p.getName());
		values.put("players", players);
		values.put("owner", (getOwner() != null) ? getOwner().getName() : "");
		packet.setValue(node, values);
	}

	public void readFromPacket(StandardModPacket packet, String node) {
		HashMap<String, Object> payload = (HashMap<String, Object>) packet.getValue(node);
		HashMap<Integer, String> persons = (HashMap<Integer, String>) payload.get("players");
		String owner = (String) payload.get("owner");
		players.clear();
		for (Entry<Integer, String> person : persons.entrySet()) {
			RPGThing.getLog().info("Adding player: " + person.getValue());
			addPlayer(Player.getPlayer(person.getValue(), true));
		}

		if (!owner.equals(""))
			setOwner(Player.getPlayer(owner, true));
	}
	
	public boolean pollModified() {
		if (isModified)
		{
			isModified = false;
			return true;
		}
		return false;
	}

}
