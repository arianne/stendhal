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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import games.stendhal.server.core.rp.achievement.Achievement;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
/**
 * DAO to handle achievements for the stendhal website
 * @author madmetzger
 *
 */
public class AchievementDAO {


	/**
	 * logs a reached achievement into the database
	 *
	 * @param transaction DBTransaction
	 * @param achievementId id of achievement
	 * @param playerName name of player
	 * @param timestamp timestamp
	 * @throws SQLException in case of an database error
	 */
	public void saveReachedAchievement(DBTransaction transaction, Integer achievementId, String playerName, boolean incReachedCount, Timestamp timestamp) throws SQLException {
		String query  = "INSERT INTO reached_achievement " +
						"(charname, achievement_id, timedate) VALUES" +
						"('[charname]','[achievement_id]', '[timedate]');";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", playerName);
		parameters.put("achievement_id", achievementId);
		parameters.put("timedate", timestamp);
		transaction.execute(query, parameters);

		if (incReachedCount) {
			query = "UPDATE achievement SET reached = reached+1 WHERE id=[achievement_id];";
			transaction.execute(query, parameters);
		}
	}

	/**
	 * Saves the base data of an achievement
	 *
	 * @param achievement Achievement to save
	 * @return the id of the stored achievement
	 * @throws SQLException in case of an database error
	 */
	public int insertAchievement(Achievement achievement) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		int achievementId = insertAchievement(transaction, achievement);
		TransactionPool.get().commit(transaction);
		return achievementId;
	}

	/**
	 * Saves the base data of an achievement
	 *
	 * @param achievement Achievement to save
	 * @param transaction a database transaction to execute the save operation in
	 * @return the id of the stored achievement
	 * @throws SQLException in case of an database error
	 */
	public int insertAchievement(DBTransaction transaction, Achievement achievement) throws SQLException {
		int achievementId = 0;
		String query = 	"INSERT INTO achievement " +
						"(identifier, title, category, description, base_score, active, reached) VALUES " +
						"('[identifier]','[title]','[category]', '[description]', [base_score], [active], 0)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("identifier", achievement.getIdentifier());
		parameters.put("title", achievement.getTitle());
		parameters.put("category", achievement.getCategory().toString());
		parameters.put("description", achievement.getDescription());
		parameters.put("base_score", achievement.getBaseScore());
		parameters.put("active", achievement.isActive() ? 1 : 0);
		transaction.execute(query, parameters);
		achievementId = transaction.getLastInsertId("achievement", "id");
		return achievementId;
	}

	/**
	 * Updates the achievement with the given id
	 *
	 * @param id id of achievement
	 * @param achievement Achievement
	 * @throws SQLException in case of an database error
	 */
	public void updateAchievement(Integer id, Achievement achievement) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		updateAchievement(transaction, id, achievement);
		TransactionPool.get().commit(transaction);
	}

	/**
	 * Updates the achievement with the given id
	 *
	 * @param transaction DBTransaction
	 * @param id id of achievement
	 * @param achievement Achievement
	 * @throws SQLException in case of an database error
	 */
	public void updateAchievement(DBTransaction transaction, Integer id, Achievement achievement) throws SQLException {
		String query = "UPDATE achievement SET " +
						"identifier = '[identifier]', " +
						"title = '[title]', " +
						"category = '[category]', " +
						"description = '[description]', " +
						"base_score = [base_score], " +
						"active = [active] " +
						"WHERE id = [id];";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("identifier", achievement.getIdentifier());
		parameters.put("title", achievement.getTitle());
		parameters.put("category", achievement.getCategory().toString());
		parameters.put("description", achievement.getDescription());
		parameters.put("base_score", achievement.getBaseScore());
		parameters.put("active", achievement.isActive() ? 1 : 0);
		parameters.put("id", id);
		transaction.execute(query, parameters);
	}

	/**
	 * Loads a map from achievement identifier to database serial
	 *
	 * @return map with key identifier string and value database id
	 * @throws SQLException in case of an database error
	 */
	public Map<String, Integer> loadIdentifierIdPairs() throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		Map<String, Integer> map = loadIdentifierIdPairs(transaction);
		TransactionPool.get().commit(transaction);
		return map;
	}

	/**
	 * Loads a map from achievement identifier to database serial
	 *
	 * @param transaction DBTransaction
	 * @return map with key identifier string and value database id
	 * @throws SQLException in case of an database error
	 */
	public Map<String, Integer> loadIdentifierIdPairs(DBTransaction transaction) throws SQLException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String query = "SELECT identifier, id FROM achievement;";
		ResultSet set = transaction.query(query, new HashMap<String, Object>());
		while (set.next()) {
			String identifier = set.getString("identifier");
			Integer id = set.getInt("id");
			map.put(identifier, id);
		}
		return map;
	}

	/**
	 * Loads all achievements a player has reached
	 *
	 * @param playerName name of player
	 * @return set identifiers of achievements reached by playerName
	 * @throws SQLException in case of an database error
	 */
	public Set<String> loadAllReachedAchievementsOfPlayer(String playerName) throws SQLException {
		DBTransaction transaction = TransactionPool.get().beginWork();
		Set<String> set = loadAllReachedAchievementsOfPlayer(transaction, playerName);
		TransactionPool.get().commit(transaction);
		return set;
	}

	/**
	 * Loads all achievements a player has reached
	 *
	 * @param transaction DBTransaction
	 * @param playerName name of player
	 * @return set identifiers of achievements reached by playerName
	 * @throws SQLException in case of an database error
	 */
	public Set<String> loadAllReachedAchievementsOfPlayer(DBTransaction transaction, String playerName) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("playername", playerName);
		String query = "SELECT identifier FROM achievement a JOIN reached_achievement ra ON ra.achievement_id = a.id WHERE ra.charname = '[playername]';";
		ResultSet resultSet = transaction.query(query, params);
		Set<String> identifiers = new HashSet<String>();
		while(resultSet.next()) {
			identifiers.add(resultSet.getString(1));
		}
		return identifiers;
	}

}
