package uk.co.cloudhunter.rpgthing.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Player {

	private Row playerRow;

	private String playerName;
	private Party playerParty;
	private int playerLevel;
	private double playerExperience;
	
	private static Map<String, Player> players = new HashMap<String, Player>();
	
	public static Player getPlayer(String username) {
		return players.containsKey(username) ? players.get(username) : new Player(username);
	}

	private Player(String name) {
		playerName = name;
		players.put(name, this);
		Database db = RPGThing.getProxy().getDatabase();
		Table playerTable = db.get("players");
		try {
			playerRow = playerTable.match(playerTable.map("name"), playerName, 1).get(0);
		} catch (IndexOutOfBoundsException oob) {
			int r = playerTable.put(new Object[] { playerTable.rows(), playerName, 0, 1, 0.0d });
			playerRow = playerTable.get(r);
		}
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
		// faction : playerRow.put(2, 0);
		playerRow.put(3, playerLevel);
		playerRow.put(4, playerExperience);
		if (playerParty == null)
			playerRow.put(5, -1);
		else
			playerRow.put(5, playerParty.getId());
	}

	public int getId() {
		return (Integer) playerRow.get(0);
	}

	public boolean isThisPlayer(EntityPlayer entity) {
		return entity.username.equals(playerName);
	}

}
