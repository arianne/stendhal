package games.stendhal.server.core.engine.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
	 * @param identifier
	 * @param title
	 * @param category
	 * @param playerName
	 * @param transaction
	 * @throws SQLException
	 */
	public void saveReachedAchievement(String identifier, String title, String category, String playerName, DBTransaction transaction) throws SQLException {
		String query  = "INSERT INTO reached_achievement " +
						"(charname, achievement_id) VALUES" +
						"('[charname]','[achievement_id]');";
		Integer achievementId = Integer.valueOf(createAchievementIfNotExists(identifier, title, category, transaction));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", playerName);
		parameters.put("achievement_id", achievementId);
		transaction.execute(query, parameters);
		TransactionPool.get().commit(transaction);
	}

	/**
	 * creates a new achievement if not yet created
	 * 
	 * @param identifier
	 * @param title
	 * @param category
	 * @param transaction
	 * @return
	 * @throws SQLException
	 */
	private int createAchievementIfNotExists(String identifier, String title, String category, DBTransaction transaction) throws SQLException {
		int achievementId = readAchievementIdForIdentifier(identifier, transaction);
		if (achievementId == 0) {
			String query = 	"INSERT INTO achievement " +
								"(identifier, title, category) VALUES " +
								"('[identifier]','[title]','[category]')";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("identifier", identifier);
			parameters.put("title", title);
			parameters.put("category", category);
			transaction.execute(query, parameters);
			achievementId = transaction.getLastInsertId("achievement", "id");
		}
		return achievementId;
	}

	/**
	 * reads the id of the achievement with the given identifier
	 * @param identifier
	 * @param transaction
	 * @return the id of the found achievement, id is 0 if no id was found
	 * @throws SQLException
	 */
	private int readAchievementIdForIdentifier(String identifier, DBTransaction transaction) throws SQLException {
		String query = "SELECT id FROM achievement WHERE identifier = '[identifier]';";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("identifier",identifier);
		int id = transaction.querySingleCellInt(query, parameters);
		return id;
	}

}
