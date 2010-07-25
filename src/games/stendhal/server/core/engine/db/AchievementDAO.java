package games.stendhal.server.core.engine.db;

import games.stendhal.server.core.events.achievements.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import marauroa.server.db.DBTransaction;
/**
 * DAO to handle achievements for the stendhal website
 * @author madmetzger
 *
 */
public class AchievementDAO {
	
	/**
	 * logs a reached achievement into the database
	 * 
	 * @param achievementId
	 * @param playerName
	 * @param transaction
	 * @throws SQLException
	 */
	public void saveReachedAchievement(Integer achievementId, String playerName, DBTransaction transaction) throws SQLException {
		String query  = "INSERT INTO reached_achievement " +
						"(charname, achievement_id) VALUES" +
						"('[charname]','[achievement_id]');";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", playerName);
		parameters.put("achievement_id", achievementId);
		transaction.execute(query, parameters);
	}

	/**
	 * Creates a new achievement
	 * 
	 * @param identifier
	 * @param title
	 * @param category
	 * @param transaction
	 * @return the id of the stored achievement
	 * @throws SQLException
	 */
	public int saveAchievement(String identifier, String title, Category category, int baseScore, DBTransaction transaction) throws SQLException {
		int achievementId = 0;
		String query = 	"INSERT INTO achievement " +
						"(identifier, title, category, base_score) VALUES " +
						"('[identifier]','[title]','[category]', [base_score])";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("identifier", identifier);
		parameters.put("title", title);
		parameters.put("category", category.toString());
		parameters.put("base_score", baseScore);
		transaction.execute(query, parameters);
		achievementId = transaction.getLastInsertId("achievement", "id");
		return achievementId;
	}

	/**
	 * Loads a map from achievement identifier to database serial
	 * @param transaction
	 * @return map with key identifier string and value database id
	 * @throws SQLException
	 */
	public Map<String, Integer> loadIdentifierIdPairs(DBTransaction transaction) throws SQLException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String query = "SELECT identifier, id FROM achievement;";
		ResultSet set = transaction.query(query, new HashMap<String, Object>());
			while (set.next()) {
				String identifier = set.getString("identifier");
				Integer id = set.getInt("id");
				map.put(identifier, id);
			};
		return map;
	}
	
	/**
	 * Loads all achievements a player has reached
	 * @param playerName
	 * @return set identifiers of achievements reached by playerName
	 * @throws SQLException 
	 */
	public Set<String> loadAllReachedAchievementsOfPlayer(String playerName, DBTransaction transaction) throws SQLException {
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
