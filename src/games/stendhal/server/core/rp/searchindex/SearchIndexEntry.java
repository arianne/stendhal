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

import java.util.Locale;


/**
 * a search index entry
 *
 * @author hendrik
 */
public class SearchIndexEntry {

	private final String searchTerm;
	private final char entityType;
	private final String entityName;
	private final int searchScore;
	private final int dbId;

	/**
	 * a search index entry
	 *
	 * @param searchTerm the search term, usually a phrase or a single word
	 * @param entityType type of entity (A achievement, I item, C creatures, N NPC, Z zone)
	 * @param entityName name of entity
	 * @param searchScore score of this match
	 *       (30: match on the complete name,
	 *        20: match on parts of the name,
	 *        10: match on parts of the descriptions)
	 */
	public SearchIndexEntry(String searchTerm, char entityType, String entityName, int searchScore) {
		this.searchTerm = searchTerm.toLowerCase(Locale.ENGLISH);
		this.entityType = entityType;
		this.entityName = entityName;
		this.searchScore = searchScore;
		this.dbId = -1;
	}

	/**
	 * a search index entry
	 *
	 * @param searchTerm the search term, usually a phrase or a single word
	 * @param entityType type of entity (A achievement, I item, C creatures, N NPC, Z zone)
	 * @param entityName name of entity
	 * @param searchScore score of this match
	 *       (30: match on the complete name,
	 *        20: match on parts of the name,
	 *        10: match on parts of the descriptions)
	 * @param dbId database table id
	 */
	public SearchIndexEntry(String searchTerm, char entityType, String entityName, int searchScore, int dbId) {
		this.searchTerm = searchTerm.toLowerCase(Locale.ENGLISH);
		this.entityType = entityType;
		this.entityName = entityName;
		this.searchScore = searchScore;
		this.dbId = dbId;
	}

	/**
	 * gets the search term
	 *
	 * @return search term
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * gets the entity type
	 *
	 * @return entity type
	 */
	public char getEntityType() {
		return entityType;
	}

	/**
	 * gets the entity name
	 *
	 * @return name of entity
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * gets the search score
	 *
	 * @return search score
	 */
	public int getSearchScore() {
		return searchScore;
	}

	/**
	 * gets the database id
	 *
	 * @return database id or -1
	 */
	public int getDbId() {
		return dbId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entityName.hashCode();
		result = prime * result + entityType;
		result = prime * result + searchScore;
		result = prime * result + searchTerm.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SearchIndexEntry)) {
			return false;
		}
		SearchIndexEntry other = (SearchIndexEntry) obj;

		if (!entityName.equals(other.entityName)) {
			return false;
		}
		if (entityType != other.entityType) {
			return false;
		}
		if (searchScore != other.searchScore) {
			return false;
		}
		if (!searchTerm.equals(other.searchTerm)) {
			return false;
		}

		return true;
	}
}
