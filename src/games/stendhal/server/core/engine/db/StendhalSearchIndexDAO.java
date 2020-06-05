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
package games.stendhal.server.core.engine.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.google.common.collect.Sets;

import games.stendhal.server.core.rp.searchindex.SearchIndexEntry;
import marauroa.server.db.DBTransaction;

/**
 * database base access for the searchindex used on the website
 *
 * @author hendrik
 */
public class StendhalSearchIndexDAO {

	/**
	 * reads existing search index entries from the database
	 *
	 * @param transaction DBTransaction
	 * @return set of existing search index entries
	 * @throws SQLException in case a database error is thrown.
	 */
	private Set<SearchIndexEntry> readExistingEntries(DBTransaction transaction) throws SQLException {
		Set<SearchIndexEntry> res = Sets.newHashSet();
		String query = "SELECT id, searchterm, entitytype, entityname, searchscore FROM searchindex";
		ResultSet resultSet = transaction.query(query, null);
		while (resultSet.next()) {
			res.add(new SearchIndexEntry(resultSet.getString(2), resultSet.getString(3).charAt(0), resultSet.getString(4), resultSet.getInt(5), resultSet.getInt(1)));
		}
		return res;
	}

	/**
	 * writes an entry to the search index table
	 *
	 * @param stmt PreparedStatement in batch mode
	 * @param entry SearchEntry
	 * @throws SQLException in case a database error is thrown.
	 */
	private void writeEntry(PreparedStatement stmt, SearchIndexEntry entry) throws SQLException {
		stmt.setString(1, entry.getSearchTerm());
		stmt.setString(2, String.valueOf(entry.getEntityType()));
		stmt.setString(3, entry.getEntityName());
		stmt.setInt(4, entry.getSearchScore());
		stmt.addBatch();
	}

	/**
	 * deletes obsolte entries
	 *
	 * @param transaction DBTransactions
	 * @param toDelete entries to delete
	 * @throws SQLException
	 */
	private void deleteObsoleteEntries(DBTransaction transaction, Set<SearchIndexEntry> toDelete) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM searchindex WHERE id IN (");
		boolean first = true;
		for (SearchIndexEntry entry : toDelete) {
			if (first) {
				first = false;
			} else {
				sql.append(",");
			}
			sql.append(String.valueOf(entry.getDbId()));
		}
		sql.append(")");

		// if there is at least one entry to delete
		if (!first) {
			transaction.execute(sql.toString(), null);
		}
	}

	/**
	 * insert new entries
	 *
	 * @param transaction DBTransactions
	 * @param toAdd entries to add
	 * @throws SQLException
	 */
	private void addNewEntries(DBTransaction transaction, Set<SearchIndexEntry> toAdd) throws SQLException {
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO searchindex"
				+ " (searchterm, entitytype, entityname, searchscore)"
				+ " VALUES (?, ?, ?, ?)", null);
		for (SearchIndexEntry entry : toAdd) {
			writeEntry(stmt, entry);
		}
		stmt.executeBatch();
		stmt.close();
	}

	/**
	 * dumps the search index
	 *
	 * @param transaction DBTransaction
	 * @param entries required entries
	 * @throws SQLException in case of an database error
	 */
	public void updateSearchIndex(DBTransaction transaction, Set<SearchIndexEntry> entries) throws SQLException {
		Set<SearchIndexEntry> oldEntries = readExistingEntries(transaction);

		Set<SearchIndexEntry> toDelete = Sets.difference(oldEntries, entries);
		Set<SearchIndexEntry> toAdd = Sets.difference(entries, oldEntries);

		deleteObsoleteEntries(transaction, toDelete);
		addNewEntries(transaction, toAdd);
	}

}
