package uk.co.cloudhunter.rpgthing.core;

public enum EnumFactions {
	OVERWORLD, UNDERWORLD, SKYWORLD;

	public static EnumFactions fromOrdinal(int ord) {
		for (EnumFactions faction : EnumFactions.values())
			if (faction.ordinal() == ord)
				return faction;
		return null;
	}
}
