/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Available natures.
 *
 * Note that Nature is related to the magic system, and is not intended
 * to support other kinds of "effects", like slowdown, drop, paralysis, etc.
 */
public enum Nature {
	/** physical attack */
	CUT,
	/** fire magic */
	FIRE,
	/** ice magic */
	ICE,
	/** light magic */
	LIGHT,
	/** dark magic */
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
