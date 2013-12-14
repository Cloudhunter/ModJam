package uk.co.cloudhunter.rpgthing.core;

import uk.co.cloudhunter.rpgthing.database.Database.Row;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class Player {

	private Row playerRow;
	private Party playerParty;

	public Player() {

	}

	public int getId() {
		return (Integer) playerRow.get(0);
	}

}
