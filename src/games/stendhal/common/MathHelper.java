/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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

	public static final int TURNS_IN_ONE_MINUTE = (int) (60 / 0.3); // 200

	/**
	 * Parses a double safely, returning a default if nothing can be sanely
	 * parsed from it.
	 *
	 * @param s the string to parse
	 * @param def the default value
	 *
	 * @return double corresponding to s, or def if s can not be parsed to a
	 * 	double
	 */
	public static double parseDoubleDefault(String s, double def) {
		if (s == null) {
			return def;
		}
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * Parses a double safely, returning 0 if nothing can be sanely parsed from
	 * it.
	 *
	 * @param s the string to parse
	 *
	 * @return double corresponding to s, or 0 if s can not be parsed to a
	 * 	double
	 */
	public static double parseDouble(String s) {
		return parseDoubleDefault(s, 0.0);
	}

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

	/**
	 * Limit an integer value to a specific range.
	 *
	 * @param value value to be limited
	 * @param minValue minimum value. Should be <= maxValue.
	 * @param maxValue maximum value. Should be >= minValue.
	 * @return if value is in range [minValue, maxValue], value is returned.
	 * 	Otherwise if value > maxValue, maxValue is returned, if value < minValue,
	 * 	minValue is returned.
	 */
	public static int clamp(int value, int minValue, int maxValue) {
		return Math.max(minValue, Math.min(value, maxValue));
	}

	/**
	 * Limit a float value to a specific range.
	 *
	 * @param value value to be limited
	 * @param minValue minimum value. Should be <= maxValue.
	 * @param maxValue maximum value. Should be >= minValue.
	 * @return if value is in range [minValue, maxValue], value is returned.
	 * 	Otherwise if value > maxValue, maxValue is returned, if value < minValue,
	 * 	minValue is returned.
	 */
	public static float clamp(float value, float minValue, float maxValue) {
		return Math.max(minValue, Math.min(value, maxValue));
	}
}
