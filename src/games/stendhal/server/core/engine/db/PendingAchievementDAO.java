/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
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

import games.stendhal.server.util.MapOfMaps;
import marauroa.server.db.DBTransaction;

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

}
