package games.stendhal.common;

/**
 * Helper functions for various mathematical tasks.
 */
public class MathHelper {

	/**
	 * parses an integer safely. returning a default if nothing can be sanely
	 * parsed from it
	 * @param s the string to parse
	 * @param def the default to set
	 * 
	 * @return An integer
	 */
	public static int parseIntDefault(String s, int def) {
		if (s == null) {
			return def;
		}

		int r;
		try {
			r = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			r = def;
		}
		return r;
	}

	/**
	 * parses an integer safely, returning 0 if nothing can be sanely parsed.
	 * from it
	 * @param s to parse
	 * 
	 * @return An integer
	 */
	public static int parseInt(String s) {
		return parseIntDefault(s, 0);
	}

}
