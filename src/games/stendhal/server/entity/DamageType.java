package games.stendhal.server.entity;

import org.apache.log4j.Logger;

/**
 * Available damage types.
 */
public enum DamageType {
	CUT,
	FIRE,
	ICE,
	LIGHT,
	DARK;

	public static DamageType parse(String type) {
		if ("cut".equals(type)) {
			return CUT;
		} else if ("fire".equals(type)) {
			return FIRE;
		} else if ("ice".equals(type)) {
			return ICE;
		} else if ("light".equals(type)) {
			return LIGHT;
		} else if ("dark".equals(type)) {
			return DARK;
		}
		
		Logger.getLogger(DamageType.class).error("Unknown damage type: " + type);
		return CUT;
	}
}
