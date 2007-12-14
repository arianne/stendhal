package games.stendhal.common;

/**
 * Helper functions for various mathematical tasks
 */
public class MathHelper {

	/**
	 * parses an integer safely. returning a default if nothing can be sanely
	 * parsed from it
	 * 
	 * @return An integer
	 */
	public static int parseIntDefault(String s, int def) {
		int r;
		try {
			r = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			r = def;
		}
		return r;
	}

	/**
	 * parses an integer safely, returning 0 if nothing can be sanely parsed
	 * from it
	 * 
	 * @return An integer
	 */
	public static int parseInt(String s) {
		return parseIntDefault(s, 0);
	}

}
