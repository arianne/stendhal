package games.stendhal.server.entity.spell;

import org.apache.log4j.Logger;

public enum Nature {
	
	LIGHT,
	DARK,
	FIRE,
	WATER,
	EARTH,
	AIR;
	
	public static Nature parse(String type) {
		try {
			return Nature.valueOf(type.toUpperCase());
		} catch (RuntimeException e) {
			Logger.getLogger(Nature.class).error("Unknown spell nature: " + type, e);
			return LIGHT;
		}
	}

}
