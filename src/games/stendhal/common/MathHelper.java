package games.stendhal.common;

/**
 * Helper functions for various mathematical tasks.
 */
public class MathHelper {
	public static final long MILLISECONDS_IN_ONE_MINUTE = 60 * 1000; 
	public static final long MILLISECONDS_IN_ONE_HOUR = 60 * MILLISECONDS_IN_ONE_MINUTE; 
	public static final long MILLISECONDS_IN_ONE_DAY = 24 * MILLISECONDS_IN_ONE_HOUR;
	public static final long MILLISECONDS_IN_ONE_WEEK = 7 * MILLISECONDS_IN_ONE_DAY;
	public static final int SECONDS_IN_ONE_MINUTE = 60; 
	public static final int SECONDS_IN_ONE_HOUR = 60 * SECONDS_IN_ONE_MINUTE; 
	public static final int SECONDS_IN_ONE_DAY = 24 * SECONDS_IN_ONE_HOUR;
	public static final int SECONDS_IN_ONE_WEEK = 7 * SECONDS_IN_ONE_DAY;
	public static final int MINUTES_IN_ONE_HOUR = 60;
	public static final int MINUTES_IN_ONE_DAY = MINUTES_IN_ONE_HOUR * 24;
	public static final int MINUTES_IN_ONE_WEEK = MINUTES_IN_ONE_DAY * 7;

	/**
	 * parses an integer safely. returning a default if nothing can be sanely
	 * parsed from it
	 *
	 * @param s the string to parse
	 * @param def the default to set
	 * 
	 * @return An integer
	 */
	public static int parseIntDefault(final String s, final int def) {
		if (s == null) {
			return def;
		}

		int r;
		try {
			r = Integer.parseInt(s);
		} catch (final NumberFormatException e) {
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
	public static int parseInt(final String s) {
		return parseIntDefault(s, 0);
	}


	/**
	 * parses a long safely. returning a default if nothing can be sanely
	 * parsed from it
	 *
	 * @param s the string to parse
	 * @param def the default to set
	 * 
	 * @return An integer
	 */
	public static long parseLongDefault(final String s, final long def) {
		if (s == null) {
			return def;
		}

		long r;
		try {
			r = Long.parseLong(s);
		} catch (final NumberFormatException e) {
			r = def;
		}
		return r;
	}

	/**
	 * parses a long safely, returning 0 if nothing can be sanely parsed.
	 * from it
	 * @param s to parse
	 * 
	 * @return An integer
	 */
	public static long parseLong(final String s) {
		return parseLongDefault(s, 0);
	}

}
