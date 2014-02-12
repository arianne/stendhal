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
	ACHIEVEMENT('A', 9),
	ITEM('I', 8),
	CREATURE('C', 7),
	NPC('N', 6),
	PLAYER('P', 5),
	PLAYER_GUIDE('p', 4),
	WORLD_GUIDE('w', 3),
	CONTRIBUTOR_GUIDE('c', 2);

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
