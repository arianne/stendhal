/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

/**
 * database access for the redundant buddy table used on the website
 */
public class StendhalBuddyDAO {

	/**
	 * loads the buddy list for the specified charname
	 *
	 * @param transaction DBTransaction
	 * @param charname name of char
	 * @return buddy list
	 * @throws SQLException in case of an database error
	 */
	public Set<String> loadBuddyList(DBTransaction transaction, String charname) throws SQLException {
		String query = "SELECT buddy FROM buddy WHERE charname='[charname]'";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);
		ResultSet resultSet = transaction.query(query, params);
		Set<String> res = new TreeSet<String>();
		while (resultSet.next()) {
			res.add(resultSet.getString(1));
		}
		return res;
	}


	/**
	 * saves the buddy list for the specified charname
	 * 
	 * @param transaction transaction
	 * @param charname name of char
	 * @param buddies buddy list
	 * @throws SQLException in case of an database error
	 */
	public void saveBuddyList(DBTransaction transaction, String charname, Set<String> buddies) throws SQLException {
		Set<String> oldList = loadBuddyList(transaction, charname);
		Set<String> newList = buddies;
		newList.add(charname);
		syncBuddyListToDB(transaction, charname, oldList, newList);
	}


	/**
	 * writes the current buddy list to the database, minimizing the write operations.
	 *
	 * @param transaction DBTransaction
	 * @param charname name of character
	 * @param oldList  old buddy list from db
	 * @param newList  current buddy list
	 * @throws SQLException 
	 * @throws SQLException in case of an database error
	 */
	private void syncBuddyListToDB(DBTransaction transaction, String charname, Set<String> oldList, Set<String> newList) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);

		// add
		Set<String> toAdd = new TreeSet<String>(newList);
		toAdd.removeAll(oldList);
		String query = "INSERT INTO buddy (charname, buddy) VALUES ('[charname]', '[buddy]')";
		for (String buddy : toAdd) {
			params.put("buddy", buddy);
			transaction.execute(query, params);
		}

		// delete
		Set<String> toDel = new TreeSet<String>(oldList);
		toDel.removeAll(newList);
		query = "DELETE FROM buddy WHERE charname='[charname]' AND buddy='[buddy]'";
		for (String buddy : toDel) {
			params.put("buddy", buddy);
			transaction.execute(query, params);
		}
	}


	/**
	 * loads the buddy list for the specified charname
	 *
	 * @param charname name of char
	 * @return buddy list
	 * @throws SQLException in case of an database error
	 */
	public Set<String> loadBuddyList(String charname) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			return loadBuddyList(transaction, charname);
		} finally {
			TransactionPool.get().commit(transaction);
		}
	}


	/**
	 * saves the buddy list for the specified charname
	 *
	 * @param charname name of char
	 * @param buddies buddy list
	 * @throws SQLException in case of an database error
	 */
	public void saveBuddyList(String charname, Set<String> buddies) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			saveBuddyList(transaction, charname, buddies);
		} finally {
			TransactionPool.get().commit(transaction);
		}
	}

}
