/***************************************************************************
 *                    (C) Copyright 2003-2020 - Stendhal                   *
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.server.db.DBTransaction;

/**
 * database access for the hall of fame used in deathmatch
 *
 * @author hendrik
 */
public class StendhalHallOfFameDAO {
	private static Logger logger = Logger.getLogger(StendhalHallOfFameDAO.class);


	/**
	 * Returns the points in the specified hall of fame.
	 *
	 * @param transaction
	 *            Transaction
	 * @param charname
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @return points or 0 in case there is no entry
	 */
	public int getHallOfFamePoints(final DBTransaction transaction, final String charname, final String fametype) {
		int res = 0;
		try {
			final String query = "SELECT points FROM halloffame WHERE charname="
					+ "'[charname]' AND fametype='[fametype]'";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("charname", charname);
			params.put("fametype", fametype);

			final ResultSet result = transaction.query(query, params);
			if (result.next()) {
				res = result.getInt("points");
			}
			result.close();
		} catch (final Exception sqle) {
			logger.warn("Error reading hall of fame", sqle);
		}

		return res;
	}

	/**
	 * Stores an entry in the hall of fame.
	 *
	 * @param transaction
	 *            Transaction
	 * @param charname
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @param points
	 *            points to store
	 * @throws SQLException
	 *             in case of an database error
	 */
	public void setHallOfFamePoints(final DBTransaction transaction, final String charname, final String fametype, final int points) throws SQLException {
		try {

			// first try an update
			String query = "UPDATE halloffame SET points='[points]'"
					+ " WHERE charname='[charname]' AND fametype='[fametype]';";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("points", Integer.toString(points));
			params.put("charname", charname);
			params.put("fametype", fametype);


			final int count = transaction.execute(query, params);

			if (count == 0) {
				// no row was modified, so we need to do an insert
				query = "INSERT INTO halloffame (charname, fametype, points)"
					+ " VALUES ('[charname]', '[fametype]', '[points]')";
				transaction.execute(query, params);
			}
		} catch (final SQLException sqle) {
			logger.warn("error adding game event", sqle);
			throw sqle;
		}
	}

	/**
	 * gets the characters who have taken part in the specified fametype
	 *
	 * @param transaction a DBTransaction
	 * @param fametype type of fame
	 * @param max maximum number of returned characters
	 * @param ascending sort ascending or descending
	 * @return list of character names
	 * @throws SQLException in case of an database error
	 */
	public List<String> getCharactersByFametype(DBTransaction transaction, String fametype, int max, boolean ascending) throws SQLException {
		List<String> res = new LinkedList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fametype", fametype);

		// generate SQL statement
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT charname FROM halloffame");
		sql.append(" WHERE fametype = '[fametype]'");
		sql.append(" ORDER BY points");
		if (!ascending) {
			sql.append(" DESC");
		}
		if (max > 0) {
			sql.append(" LIMIT " + max);
		}

		// read result
		ResultSet resultSet = transaction.query(sql.toString(), params);
		while (resultSet.next()) {
			res.add(resultSet.getString(1));
		}
		return res;
	}

}
