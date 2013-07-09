/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import games.stendhal.server.util.MapOfMaps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

/**
 * DAO to gather information on pending achievements
 *
 * @author kymara
 */
public class PendingAchievementDAO {

	/**
	 * Delete used records for a pending achievement for a given player
	 *
	 * @param transaction DBTransaction
	 * @param charname name of player
	 * @throws SQLException in case of an database error
	 */
	public void deletePendingAchievementDetails(DBTransaction transaction, String charname) throws SQLException {
		String query  = "DELETE FROM pending_achievement " +
						"WHERE charname = '[charname]';";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", charname);
		transaction.execute(query, parameters);
	}


	/**
	 * Get details on pending achievements for a given player
	 *
	 * @param transaction DBTransaction
	 * @param charname name of player
	 * @return details as param and count
	 * @throws SQLException in case of an database error
	 */
	public Map<String, Map<String, Integer>> getPendingAchievementDetails(DBTransaction transaction, String charname) throws SQLException {

		MapOfMaps<String, String, Integer> map = new MapOfMaps<String, String, Integer>();
		String query  = "SELECT identifier, param, cnt FROM pending_achievement " +
		                "JOIN achievement on achievement_id = achievement.id " +
						"WHERE charname = '[charname]';";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", charname);
		ResultSet set = transaction.query(query, parameters);
		while (set.next()) {
			String identifier = set.getString("identifier");
			String param = set.getString("param");
			Integer cnt = set.getInt("cnt");
			map.put(identifier, param, cnt);
		}
		return map;
	}


	/**
	 * Get details on pending achievements for a given player
	 *
	 * @param charname name of player
	 * @return details as param and count
	 * @throws SQLException in case of an database error
	 */
	public Map<String, Map<String, Integer>> getPendingAchievementDetails(String charname) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		Map<String, Map<String, Integer>> map = getPendingAchievementDetails(transaction, charname);
		TransactionPool.get().commit(transaction);
		return map;
	}

	/**
	 * Delete used records for a pending achievement for a given player
	 *
	 * @param charname name of player
	 * @throws SQLException in case of an database error

	 */
	public void deletePendingAchievementDetails(String charname) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		deletePendingAchievementDetails(transaction, charname);
		TransactionPool.get().commit(transaction);
	}

}
