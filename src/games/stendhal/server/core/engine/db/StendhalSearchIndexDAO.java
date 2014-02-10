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

import games.stendhal.server.core.rp.SearchIndexEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * database base access for the searchindex used on the website
 *
 * @author hendrik
 */
public class StendhalSearchIndexDAO {
	private static Logger logger = Logger.getLogger(StendhalSearchIndexDAO.class);


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
	 * dumps the search index
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpSearchIndex(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		Set<SearchIndexEntry> toDelete = Sets.newHashSet();
		Set<SearchIndexEntry> toAdd = Sets.newHashSet();

		// delete obsolte entries
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
		transaction.execute(sql.toString(), null);

		// insert new entries
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO searchindex"
				+ " (searchterm, entitytype, entityname, searchscore)"
				+ " VALUES (?, ?, ?, ?)", null);
		for (SearchIndexEntry entry : toAdd) {
			writeEntry(stmt, entry);
		}
		stmt.executeBatch();

		logger.info("Completed dumping of search index in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	/**
	 * dumps the search index
	 */
	public void dumpSearchIndex() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			dumpSearchIndex(transaction);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}
	}
}
