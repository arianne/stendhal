package games.stendhal.server.entity;

import org.apache.log4j.Logger;

/**
 * Available damage types.
 */
public enum DamageType {
	CUT,
	FIRE,
	ICE;

	public static DamageType parse(String type) {
		if (type.equals("cut")) {
			return CUT;
		} else if (type.equals("fire")) {
			return FIRE;
		} else if (type.equals("ice")) {
			return ICE;
		}
		
		Logger.getLogger(DamageType.class).equals("Unknown damage type: " + type);
		return CUT;
	}
}
