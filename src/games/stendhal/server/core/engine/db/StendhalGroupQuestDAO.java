/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;

/**
 * DAO to handle group quests
 *
 * @author madmetzger
 */
public class StendhalGroupQuestDAO {

	/**
	 * reads a group quest
	 *
	 * @param transaction DBTransaction
	 * @param name of group quest
	 * @throws SQLException in case of an database error
	 */
	public Map<String, Integer> load(DBTransaction transaction, String questname) throws SQLException {
		String query  = "SELECT itemname, sum(quantity) FROM group_quest WHERE questname='[questname]' GROUP BY itemname";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("questname", questname);
		ResultSet rs = transaction.query(query, parameters);

		Map<String, Integer> res = new HashMap<>();
		while (rs.next()) {
			res.put(rs.getString(1), rs.getInt(2));
		}
		return res;
	}


	/**
	 * logs group quest progress
	 *
	 * @param transaction transaction
	 * @param questname name of quest
	 * @param charname  name of player
	 * @param itemname name of item
	 * @param quantity quantity of that item
	 * @throws SQLException in case of an database error
	 */
	public void update(DBTransaction transaction, String questname, String charname, String itemname, Integer quantity, Timestamp timestamp) throws SQLException {
		// try update in case we already have this combination
		String query = "UPDATE group_quest SET quantity = quantity+[quantity]"
			+ " WHERE questname = '[questname]' AND charname = '[charname]'"
			+ " AND itemname = '[itemname]' AND day = '[day]';";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("questname", questname);
		params.put("charname", charname);
		params.put("itemname", itemname);
		params.put("quantity", quantity);
		params.put("day", new SimpleDateFormat("yyyy-MM-dd").format(timestamp));

		final int rowCount = transaction.execute(query, params);

		// in case we did not have this combination yet, make an insert
		if (rowCount == 0) {
			query = "INSERT INTO group_quest (questname, charname, itemname, quantity, day)"
				+ " VALUES ('[questname]', '[charname]', '[itemname]', [quantity], '[day]')";
			transaction.execute(query, params);
		}

	}
}
