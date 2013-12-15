package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Party {

	private Row partyRow;
	private ArrayList<Player> players = new ArrayList<Player>();
	private Player owner;
	@SideOnly(Side.CLIENT)
	private static Map<Integer, Party> partiesClient = new HashMap<Integer, Party>();
	
	private static Map<Integer, Party> partiesServer = new HashMap<Integer, Party>();
	
	public boolean isClient;
	
	public static Party getPartyById(int id, boolean isClient) {
		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		return parties.containsKey(id) ? parties.get(id) : new Party(id, isClient);
	}
	
	public static Party newParty(boolean isClient)
	{
		return new Party(isClient);
	}

	private Party(boolean isClient) {
		this.isClient = isClient;
		Database db = RPGThing.getProxy().getDatabase();
		Table partyTable = db.get("parties");
		int r = partyTable.put(new Object[] { partyTable.rows(), ""});
		partyRow = partyTable.get(r);
		
		unpack();
		
		Map<Integer, Party> parties = isClient ? partiesClient : partiesServer;
		parties.put(this.getId(), this);
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
	}

	public void addPlayer(Player p) {
		synchronized (players) {
			if (!players.contains(p))
				players.add(p);
			p.setParty(this);
		}
		commit();
	}

	public void removePlayer(Player p) {
		synchronized (players) {
			players.remove(p);
		}
		commit();
	}

	public Player[] getPlayers() {
		return players.toArray(new Player[0]);
	}

	public void setOwner(Player p) {
		this.owner = p;
	}

	public Player getOwner() {
		return owner;
	}

	public int getId() {
		return (Integer) partyRow.get(0);
	}

	public void disband() {
		RPGThing.getProxy().getDatabase().get("parties").remove(partyRow.id());
	}
	
	private void unpack() {
		String members = (String) partyRow.get(1);
		if (members != "")
		{
			players = new ArrayList<Player>();
			for (String str: members.split(","))
				players.add(Player.getPlayer(str, isClient));
		}
		
	}

	private void commit() {
		StringBuilder members = new StringBuilder();
		synchronized (players) {
			for (Player p : players)
				members.append(p.getId()).append(",");
		}
		partyRow.put(1, members.toString());
	}

}
