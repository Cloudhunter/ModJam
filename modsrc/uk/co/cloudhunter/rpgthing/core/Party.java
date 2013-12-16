package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;
import uk.co.cloudhunter.rpgthing.network.StandardModPacket;

public class Party {

	public static ReentrantLock partyLock = new ReentrantLock();
	private static Map<Integer, Party> partiesClient = new HashMap<Integer, Party>();
	private static Map<Integer, Party> partiesServer = new HashMap<Integer, Party>();

	private Row partyRow;
	private ArrayList<Player> players = new ArrayList<Player>();
	private Player owner;

	public boolean isClient;
	private boolean isModified;
	private boolean isDisbanded;

	public static Party getPartyById(int id, boolean isClient) {
		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		try {
			partyLock.lock();
			if (parties.containsKey(id)) {
				Party p = parties.get(id);
				partyLock.unlock();
				return p;
			} else {
				partyLock.unlock();
				return new Party(id, isClient);
			}
		} finally {
			if (partyLock.isHeldByCurrentThread())
				partyLock.unlock();
		}
	}

	public static Party[] getAllParties(boolean isClient) {
		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		partyLock.lock();
		Party[] result = parties.values().toArray(new Party[0]);
		partyLock.unlock();
		return result;
	}

	public static Party newParty(boolean isClient) {
		return new Party(isClient);
	}

	public static void removeParty(Party party) {
		partyLock.lock();
		if (partiesServer.containsValue(party)) {
			RPGThing.getLog().info("Removing party " + party.getId() + " from cache");
			partiesServer.remove(party.getId());
		}
		partyLock.unlock();
	}

	private Party(boolean isClient) {
		this.isClient = isClient;
		Database db = RPGThing.getProxy().getDatabase();
		Table partyTable = db.get("parties");
		int r = partyTable.put(new Object[] { partyTable.rows(), "" });
		partyRow = partyTable.get(r);

		unpack();

		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		partyLock.lock();
		parties.put(this.getId(), this);
		partyLock.unlock();
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
		partyLock.lock();
		parties.put(this.getId(), this);
		partyLock.unlock();

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
			p.setParty(null);
		}
		if (owner == p)
			removeOwner();
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

	public void removeOwner() {
		if (players.size() == 0)
			disband();
		this.owner = players.iterator().next();
	}

	public int getId() {
		return (Integer) partyRow.get(0);
	}

	public void disband() {
		RPGThing.getProxy().getDatabase().get("parties").remove(partyRow.id());
		if (players.size() > 0) {
			Iterator it = players.iterator();
			while (it.hasNext())
				removePlayer((Player) it.next());
		}
		isModified = true;
		isDisbanded = true;
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
		if (isModified) {
			RPGThing.getLog().info("Clearing dirty party flag.");
			isModified = false;
			return true;
		}
		return false;
	}

	public void sendMessageToPlayers(ChatMessageComponent component) {
		for (Player p : getPlayers()) {
			EntityPlayer player = p.getMinecraftPlayer();
			if (player != null)
				player.sendChatToPlayer(component);
		}
	}

	public boolean hasDisbanded() {
		return isDisbanded;
	}

}
