/***************************************************************************
 *                    (C) Copyright 2014 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.searchindex;

/**
 * type of an search index entry
 *
 * @author hendrik
 */
public enum SearchIndexEntryType {
	/** achievements */
	ACHIEVEMENT('A', 90),
	/** items */
	ITEM('I', 80),
	/** creatures */
	CREATURE('C', 70),
	/** npcs */
	NPC('N', 60),
	/** player characters */
	PLAYER('P', 50),
	/** stendhal manual, beginners guide, etc. */
	PLAYER_GUIDE('G', 40),
	/** in-character guide */
	WORLD_GUIDE('W', 30),
	/** guides for contributors and developers */
	CONTRIBUTOR_GUIDE('C', 20);

	private char entityType;
	private int minorScore;

	private SearchIndexEntryType(char entityType, int minorScore) {
		this.entityType = entityType;
		this.minorScore = minorScore;
	}

	/**
	 * gets the type character of this entity type
	 *
	 * @return the entityType
	 */
	protected char getEntityType() {
		return entityType;
	}

	/**
	 * gets the minor score of this entity type
	 *
	 * @return the minorScore
	 */
	protected int getMinorScore() {
		return minorScore;
	}


}
