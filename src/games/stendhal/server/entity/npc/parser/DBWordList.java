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

package games.stendhal.server.entity.npc.parser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;

/**
 * Database access for WordList.
 */
// TODO: convert this class into a StendhalWordListDAO
public final class DBWordList extends WordList {

	private static final Logger logger = Logger.getLogger(DBWordList.class);

	/**
	 * Attach WordList to database and enable persistence. WordList is per
	 * default only using the pre-configured Resource word list and does not
	 * store new words into the database to enable JUint tests without database
	 * access. Creating a DBWordList object reads the word list from the database
	 * and enables further write access to it.
	 */
	public DBWordList() {
		// read word list from database
		final int dbWordCount = readFromDB();
		final String dbHash = hash;

		// At this point instance already contains the word list of "words.txt",
		// so let's use this to compare the version number against the database
		// content.

		// If the database is still empty, store the default entries into it.
		// If not, check the version number of the word list between database
		// and "words.txt". If not equal, re-initialize the DB word list.
		if ((dbWordCount == 0) || !instance.hash.equals(dbHash)) {
			// store the new word list into the DB
			writeToDB(instance);

			// take over the word list from the old instance
			takeOver(instance);
		}

		// switch instance to the database word list
		instance = this;
	}


	/**
	 * Store the given word list into the database table "words".
	 * @param wl Word list
	 * @return success flag
	 */
	public static boolean writeToDB(final WordList wl) {
		final DBTransaction transaction = TransactionPool.get().beginWork();
		boolean ret = false;

		try {
			// empty table "words"
			transaction.execute("truncate table words", null);

			ret = insertIntoDB(transaction, wl.words);

			String sql = "INSERT INTO words(normalized, type, plural)"
					+ " VALUES ('[normalized]', '[type]', '[plural]')";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("normalized", HASH_KEYWORD);
			params.put("type", HASH_KEYWORD);
			params.put("plural", wl.hash);

			transaction.execute(sql, params);
			TransactionPool.get().commit(transaction);
		} catch (final SQLException e) {
			logger.error("error closing DB accessor", e);
			TransactionPool.get().rollback(transaction);
		}

		return ret;
	}

	/**
	 * Store the new word in the database. This implementation does nothing,
	 * it is overriden by the DBWordList method.
	 * @param key
	 * @param entry
	 * @return success flag
	 */
	@Override protected boolean persistNewWord(final String key, final WordEntry entry) {
		// save the new word into the database
		final Map<String, WordEntry> wl = new HashMap<String, WordEntry>();

		wl.put(key, entry);

		return insertIntoDB(wl);
	}

	/**
	 * Insert a number of word entries into the database.
	 * 
	 * @param words
	 * @return success flag
	 */
	private static boolean insertIntoDB(final Map<String, WordEntry> words) {
		final DBTransaction transaction = TransactionPool.get().beginWork();
		boolean ret = false;

		try {
			ret = insertIntoDB(transaction, words);

			TransactionPool.get().commit(transaction);
		} catch (final SQLException e) {
			logger.error("error while inserting new word into DB", e);
			TransactionPool.get().rollback(transaction);
			ret = false;
		}

		return ret;
	}

	/**
	 * Insert a number of word entries into the database, using the given
	 * Transaction object.
	 * 
	 * @param transaction
	 * @param words
	 * @return success flag
	 * @throws SQLException in case of an SQL error
	 */
	private static boolean insertIntoDB(final DBTransaction transaction,
			final Map<String, WordEntry> words) throws SQLException {

		PreparedStatement stmt = transaction.prepareStatement(
				"insert into words(normalized, type, plural, value)\n"
						+ "values(?, ?, ?, ?)", null);

		int count = 0;

		try {
			for (Map.Entry<String,WordEntry> it : words.entrySet()) {
				final WordEntry entry = it.getValue();

				// We ignore all plural entries, they are already present as
				// attribute of the singular form.
				if ((entry.getType() == null) || !entry.getType().isPlural()) {
					stmt.setString(1, it.getKey());
					stmt.setString(2, entry.getTypeString());
					stmt.setString(3, entry.getPlurSing());

					final Integer value = entry.getValue();
					if (value == null) {
						stmt.setNull(4, Types.INTEGER);
					} else {
						stmt.setInt(4, value);
					}

					stmt.execute();

					entry.setId(transaction.getLastInsertId("words", "id"));
					count++;
				}
			}
		} finally {
			stmt.close();
		}

		stmt = transaction.prepareStatement(
				"update words set alias_id = ? where id = ?", null);

		try {
			for (Map.Entry<String,WordEntry> it : words.entrySet()) {
				final WordEntry entry = it.getValue();
				final String normalized = entry.getNormalized();

				// Now we store the alias_id for alias entries.
				if (!normalized.equals(it.getKey())) {
					final WordEntry alias = words.get(normalized);

					if (alias == null) {
						logger.error("word alias not found: " + it.getKey() + " -> "
								+ normalized);
						return false;
					} else {
						stmt.setInt(1, alias.getId());
						stmt.setInt(2, entry.getId());

						stmt.execute();
					}
				}
			}

			logger.debug("wrote " + count + " words into database");

			return true;
		} finally {
			stmt.close();
		}
	}

	/**
	 * Read word entries from the database.
	 * 
	 * @return the number of entries read or -1 on error
	 */
	private int readFromDB() {
		final DBTransaction transaction = TransactionPool.get().beginWork();

		try {
			final String query = "select	w.id, w.normalized, w.type, w.plural, w.value,"
					+ "	s.normalized from words w left outer join words s on s.id = w.alias_id";

			final ResultSet res = transaction.query(query, null);

			int count = 0;

			try {
				while (res.next()) {
					final WordEntry entry = new WordEntry();
	
					entry.setId(res.getInt(1));
	
					final String key = res.getString(2);
					entry.setNormalized(key);
	
					entry.setType(new ExpressionType(res.getString(3)));
	
					entry.setPlurSing(res.getString(4));
	
					final int value = res.getInt(5);
					if (!res.wasNull()) {
						entry.setValue(value);
					}
	
					final String singular = res.getString(6);
					if (singular != null) {
						entry.setNormalized(singular);
					}
	
					addEntry(key, entry);
					++count;
				}
	
				final WordEntry versionEntry = find(HASH_KEYWORD);
	
				if (versionEntry == null) {
					hash = "";
				} else {
					hash = versionEntry.getPlurSing();
					words.remove(versionEntry.getNormalized());
					--count;
				}
	
				logger.debug("read " + count + " word entries from database");
			} finally {
				res.close();
			}

			TransactionPool.get().commit(transaction);

			return count;
		} catch (final SQLException e) {
			logger.error("error while reading from DB table words", e);
			TransactionPool.get().rollback(transaction);
			return -1;
		}
	}

}
