package uk.co.cloudhunter.rpgthing.util;

import uk.co.cloudhunter.rpgthing.RPGThing;
import uk.co.cloudhunter.rpgthing.database.Database;
import uk.co.cloudhunter.rpgthing.database.Database.Table;

public class TableFactory {

	private Database database;

	private Table tablePlayers;
	private Table tableParties;

	public TableFactory() {
		Database db = RPGThing.getProxy().getDatabase();

		tablePlayers = db.create("players");
		tablePlayers.struct(0, Integer.class, "uid");
		tablePlayers.struct(1, String.class, "name");
		tablePlayers.struct(2, Integer.class, "faction");

		tableParties = db.create("parties");
		tableParties.struct(0, Integer.class, "uid");
		tableParties.struct(1, String.class, "members");
	}

}
