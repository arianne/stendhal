package games.stendhal.common.constants;

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

	/**
	 * Parses the DamageType, defaulting to CUT for unknown types.
	 *
	 * @param type type name
	 * @return DamageType
	 */
	public static DamageType parse(String type) {
		try {
			return DamageType.valueOf(type);
		} catch (RuntimeException e) {
			Logger.getLogger(DamageType.class).error("Unknown damage type: " + type, e);
			return CUT;
		}
	}
}
