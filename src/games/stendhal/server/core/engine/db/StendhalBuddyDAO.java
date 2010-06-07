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
		String query = "SELECT buddyname FROM buddy WHERE charname='[charname]'";
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
}
