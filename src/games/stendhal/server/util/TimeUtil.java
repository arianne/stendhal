/*
 * @(#) src/games/stendhal/server/util/TimeUtil.java
 *
 * $Id$
 */
package games.stendhal.server.util;

//
//

import games.stendhal.common.grammar.Grammar;

/**
 * Utility functions for time in the game.
 */
public class TimeUtil {

	protected static final int SECONDS_IN_WEEK = 60 * 60 * 24 * 7;

	protected static final int SECONDS_IN_DAY = 60 * 60 * 24;

	protected static final int SECONDS_IN_HOUR = 60 * 60;

	protected static final int SECONDS_IN_MINUTE = 60;

	/**
	 * Create a text representing a saying of approximate time until.
	 *
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 *
	 * @return A text representation.
	 */
	public static String approxTimeUntil(final int seconds) {
		final StringBuilder sbuf = new StringBuilder();
		approxTimeUntil(sbuf, seconds);

		return sbuf.toString();
	}

	/**
	 * Append a text representing a saying of approximate time until.
	 *
	 * @param sbuf
	 *            The buffer to append to.
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 */
	public static void approxTimeUntil(final StringBuilder sbuf, final int seconds) {
		if (approxUnit(sbuf, seconds, SECONDS_IN_WEEK, "week")) {
			return;
		}

		if (approxUnit(sbuf, seconds, SECONDS_IN_DAY, "day")) {
			return;
		}

		if (approxUnit(sbuf, seconds, SECONDS_IN_HOUR, "hour")) {
			return;
		}

		if (approxUnit(sbuf, seconds, SECONDS_IN_MINUTE, "minute")) {
			return;
		}

		sbuf.append("less than a minute");
	}

	/**
	 * For a given amount and unit size, generate the approximate value.
	 *
	 * @param sbuf
	 *            The buffer to append to.
	 * @param amount
	 *            The total amount.
	 * @param size
	 *            The unit size.
	 * @param name
	 *            The unit name.
	 *
	 * @return <code>true</code> if unit used, <code>false</code> if the
	 *         amount was too small to apply.
	 */
	protected static boolean approxUnit(final StringBuilder sbuf, final int amount,
			final int size, final String name) {
		int count = amount / size;
		int remainder;

		if (count == 0) {
			return false;
		}

		remainder = amount - (count * size);

		if (remainder >= (size * 95 / 100)) {
			count++;
			sbuf.append(count);
		} else if (remainder >= (size * 3 / 4)) {
			count++;
			sbuf.append("just under ");
			sbuf.append(count);
		} else if (remainder >= (size * 1 / 4)) {
			sbuf.append("about ");
			sbuf.append(count);
			sbuf.append(" and a half");

			/*
			 * Force plural context (e.g. "1 and a half moments")
			 */
			count = 2;
		} else if (remainder >= (size * 5 / 100)) {
			sbuf.append("just over ");
			sbuf.append(count);
		} else {
			sbuf.append(count);
		}

		sbuf.append(' ');
		sbuf.append(Grammar.plnoun(count, name));
		return true;
	}

	/**
	 * Create a text representing a saying of time until.
	 *
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 *
	 * @return A text representation.
	 */
	public static String timeUntil(final int seconds) {
		return timeUntil(seconds, false);
	}

	/**
	 * Create a text representing a saying of time until.
	 *
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 * @param forceSeconds
	 *            Show seconds even if over a minute.
	 *
	 * @return A text representation.
	 */
	public static String timeUntil(final int seconds, final boolean forceSeconds) {
		final StringBuilder sbuf = new StringBuilder();
		timeUntil(sbuf, seconds, forceSeconds);

		return sbuf.toString();
	}

	/**
	 * Append a text representing a saying of time until.
	 *
	 * @param sbuf
	 *            The buffer to append to.
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 */
	public static void timeUntil(final StringBuilder sbuf, final int seconds) {
		timeUntil(sbuf, seconds, false);
	}

	/**
	 * Append a text representing a saying of time until.
	 *
	 * @param sbuf
	 *            The buffer to append to.
	 * @param seconds
	 *            The number of seconds till/past (in positive values).
	 * @param forceSeconds
	 *            Show seconds even if over a minute.
	 */
	public static void timeUntil(final StringBuilder sbuf, int seconds,
			final boolean forceSeconds) {
		boolean appended = false;
		int count = seconds / SECONDS_IN_WEEK;

		if (count != 0) {
			seconds -= (count * SECONDS_IN_WEEK);

			sbuf.append(count);
			sbuf.append(' ');
			sbuf.append(Grammar.plnoun(count, "week"));

			appended = true;
		}

		count = seconds / SECONDS_IN_DAY;
		if (count != 0) {
			seconds -= (count * SECONDS_IN_DAY);

			if (appended) {
				sbuf.append(", ");
			} else {
				appended = true;
			}

			sbuf.append(count);
			sbuf.append(' ');
			sbuf.append(Grammar.plnoun(count, "day"));
		}
		count = seconds / SECONDS_IN_HOUR;
		if (count != 0) {
			seconds -= (count * SECONDS_IN_HOUR);

			if (appended) {
				sbuf.append(", ");
			} else {
				appended = true;
			}

			sbuf.append(count);
			sbuf.append(' ');
			sbuf.append(Grammar.plnoun(count, "hour"));
		}
		count = seconds / SECONDS_IN_MINUTE;
		if (count != 0) {
			seconds -= (count * SECONDS_IN_MINUTE);

			if (appended) {
				sbuf.append(", ");
			} else {
				appended = true;
			}

			sbuf.append(count);
			sbuf.append(' ');
			sbuf.append(Grammar.plnoun(count, "minute"));
		}

		if (!appended || (forceSeconds && (seconds != 0))) {
			if (appended) {
				sbuf.append(", ");
			}

			sbuf.append(seconds);
			sbuf.append(' ');
			sbuf.append(Grammar.plnoun(count, "second"));
		}
	}

}
