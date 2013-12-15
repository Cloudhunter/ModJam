package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;

import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Party {

	private Table partyTable;
	private Row partyRow;
	private ArrayList<Player> players;
	private Player owner;

	public Party() {

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
	
	public Player getOwner() {
		return owner;
	}

	public int getId() {
		return (Integer) partyRow.get(0);
	}

	public void disband() {
		partyTable.remove(partyRow.id());
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
