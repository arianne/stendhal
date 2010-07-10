package games.stendhal.server.core.engine.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

public class AchievementDAO {
	
	public void saveReachedAchievement(String title, String category, String playerName, DBTransaction transaction) throws SQLException {
		String query  = "INSERT INTO achievement " +
						"(charname, category, title) VALUES" +
						"('[charname]','[category]','[title]');";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("charname", playerName);
		parameters.put("category", category);
		parameters.put("title", title);
		transaction.execute(query, parameters);
		TransactionPool.get().commit(transaction);
	}

}
