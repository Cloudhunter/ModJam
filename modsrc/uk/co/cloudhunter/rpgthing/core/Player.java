package uk.co.cloudhunter.rpgthing.core;

import net.minecraft.entity.player.EntityPlayer;
import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Player {

	private Row playerRow;
	private String playerName;
	private Party playerParty;

	public Player(String name) {
		playerName = name;
	}
	
	public void setParty(Party party)
	{
		playerParty = party;
	}
	
	public Party getParty()
	{
		return playerParty;
	}

	public int getId() {
		return (Integer) playerRow.get(0);
	}

	public boolean isThisPlayer(EntityPlayer entity) {
		return entity.username.equals(playerName);
	}

}
