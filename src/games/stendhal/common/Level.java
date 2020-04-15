/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import org.apache.log4j.Logger;

/** Utility class for getting the player level for some given exp. points. */
public class Level {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Level.class);


	// Max Level is LEVELS - 1.
	// xp formula overflows for level = 599.
	public static final int LEVELS = 598;

	private static int[] xp;

	private static double[] wisdom;

	static {
		/*
		 * Calculate eXPerience
		 */
		xp = new int[LEVELS + 1];

		xp[0] = 0;
		xp[1] = 50;
		xp[2] = 100;
		xp[3] = 200;
		xp[4] = 400;
		xp[5] = 800;

		for (int i = 5; i < LEVELS; i++) {
			final int exp = ((i * 16 + i * i * 5 + i * i * i * 10 + 300) / 100) * 100;
			xp[i + 1] = exp;
		}

		if (logger.isDebugEnabled()) {
			for (int i = 0; i < LEVELS; i++) {
				logger.debug("Level " + i + ": " + xp[i] + " xp");
			}
		}

		/*
		 * Calculate Wisdom
		 */
		wisdom = new double[LEVELS];

		for (int i = 0; i < LEVELS; i++) {
			wisdom[i] = 1.0 - (1 / Math.pow(1.01, i));
		}

		if (logger.isDebugEnabled()) {
			for (int i = 0; i < LEVELS; i++) {
				logger.debug("Level " + i + ": "
						+ (int) ((wisdom[i] * 100.0) + 0.5) + " wisdom");
			}
		}
	}

	/**
	 * prints the level table
	 *
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		for (int i = 0; i < LEVELS; i++) {
			System.out.println("<tr><td>" + i + "</td><td>" + xp[i]
					+ "</td></tr>");
		}
	}

	/**
	 * gets the highest level
	 *
	 * @return highest level
	 */
	public static int maxLevel() {
		return LEVELS - 1;
	}

	/**
	 * calculates the level according to the experience.
	 *
	 * @param exp
	 *            experience needed
	 * @return level
	 */
	public static int getLevel(final int exp) {

		int first = 0;
		int last = LEVELS - 1;
		if (exp <= xp[first]) {
			return first;
		}
		if (exp >= xp[last]) {
			return last;
		}
		while (last - first > 1) {
			final int current = first + ((last - first) / 2);
			if (exp < xp[current]) {
				last = current;
			} else {
				first = current;
			}
		}
		return first;
	}

	/**
	 * Calculates the experienced needed for a level.
	 *
	 * @param level level
	 * @return experience needed
	 */
	public static int getXP(final int level) {
		if ((level >= 0) && (level < xp.length)) {
			return xp[level];
		}
		return -1;
	}

	/**
	 * Calculates how many levels to add when a certain amount of experience is
	 * added.
	 *
	 * @param exp
	 *            the current Experience
	 * @param added
	 *            the added Experience
	 * @return difference of levels
	 */
	public static int changeLevel(final int exp, final int added) {
		int i;
		for (i = 0; i < LEVELS; i++) {
			if (exp < xp[i]) {
				break;
			}
		}

		for (int j = i; j <= LEVELS; j++) {
			if (exp + added < xp[j]) {
				return j - i;
			}
		}

		return 0;
	}

	/**
	 * Get an entity's wisdom factor based on their level. As no one really has
	 * 100% (i.e. 1.0) wisdom, it should be scaled as needed.
	 *
	 * @param level
	 *            A player level.
	 *
	 * @return A value between <code>0.0</code> (inclusive) and
	 *         <code>1.0</code> (exclusive).
	 */
	public static double getWisdom(final int level) {
		if (level > LEVELS) {
			return wisdom[LEVELS];
		}

		return wisdom[level];
	}
}
