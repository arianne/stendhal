package games.stendhal.common.constants;

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Available natures.
 */
public enum Nature {
	CUT,
	FIRE,
	ICE,
	LIGHT,
	DARK;

	/**
	 * Parses the Nature, defaulting to CUT for unknown types.
	 *
	 * @param type type name
	 * @return Nature
	 */
	public static Nature parse(String type) {
		try {
			return Nature.valueOf(type.toUpperCase(Locale.ENGLISH));
		} catch (RuntimeException e) {
			Logger.getLogger(Nature.class).error("Unknown damage type: " + type, e);
			return CUT;
		}
	}
}
