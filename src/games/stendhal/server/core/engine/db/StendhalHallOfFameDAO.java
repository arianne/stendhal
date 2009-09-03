/***************************************************************************
 *                    (C) Copyright 2003-2009 - Stendhal                   *
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

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;

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
	 * Returns the points in the specified hall of fame.
	 * 
	 * @param charname name of the player
	 * @param fametype type of the hall of fame
	 * @return points or 0 in case there is no entry
	 */
	public int getHallOfFamePoints(final String charname, final String fametype) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		int res = -1;
		try {
			res = getHallOfFamePoints(transaction, charname, fametype);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			TransactionPool.get().rollback(transaction);
		}
		return res;
	}

	/**
	 * Stores an entry in the hall of fame.
	 * 
	 * @param charname name of the player
	 * @param fametype type of the hall of fame
	 * @param points points to store
	 * @throws SQLException in case of an database error
	 */
	public void setHallOfFamePoints(final String charname, final String fametype, final int points) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		setHallOfFamePoints(transaction, charname, fametype, points);
		TransactionPool.get().commit(transaction);
	}

}
