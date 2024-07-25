/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp;


/**
 * Scoring values for Hall of Fame.
 */
public class HOFScore {

	/** Standard score awarding no points. */
	public static final HOFScore NONE = new HOFScore(0);
	/** Standard score awarding 1 point. */
	public static final HOFScore EASY = new HOFScore(1);
	/** Standard score awarding 2 points. */
	public static final HOFScore MEDIUM = new HOFScore(2);
	/** Standard score awarding 5 points. */
	public static final HOFScore HARD = new HOFScore(5);
	/** Standard score awarding 7 points. */
	public static final HOFScore EXTREME = new HOFScore(7);

	/** Points value of this scoring tier. */
	public final int value;


	/**
	 * Creates a new Hall of Fame score.
	 *
	 * @param value
	 *   Points value of this scoring tier.
	 */
	public HOFScore(final int value) {
		this.value = value;
	}
}
