package uk.co.cloudhunter.rpgthing.core;

import net.minecraft.entity.player.EntityPlayer;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Player {

	private Row playerRow;

	private String playerName;
	private Party playerParty;
	private int playerLevel;
	private double playerExperience;

	public Player(String name) {
		playerName = name;
	}

	public void setParty(Party party) {
		playerParty = party;
	}

	public Party getParty() {
		return playerParty;
	}

	public int getLevel() {
		return playerLevel;
	}

	public void setLevel(int l) {
		playerLevel = l;
	}

	public double getExperience() {
		return playerExperience;
	}

	public void setExperience(double e) {
		playerExperience = e;
	}

	public int getId() {
		return (Integer) playerRow.get(0);
	}

	public boolean isThisPlayer(EntityPlayer entity) {
		return entity.username.equals(playerName);
	}

}
