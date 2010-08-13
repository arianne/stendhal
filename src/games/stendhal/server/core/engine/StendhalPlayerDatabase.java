package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.engine.db.CidDAO;
import games.stendhal.server.core.engine.db.PostmanDAO;
import games.stendhal.server.core.engine.db.StendhalBuddyDAO;
import games.stendhal.server.core.engine.db.StendhalCharacterDAO;
import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.db.StendhalNPCDAO;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.JDBCSQLHelper;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

public class StendhalPlayerDatabase {
	
	private static final Logger logger = Logger.getLogger(StendhalPlayerDatabase.class);

	public void initialize() {
		final DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			new JDBCSQLHelper(transaction).runDBScript("games/stendhal/server/stendhal_init.sql");

			if (!transaction.doesColumnExist("kills", "day")) {
				transaction.execute("ALTER TABLE kills ADD COLUMN (day DATE);", null);
			}

			if (!transaction.doesColumnExist("npcs", "image")) {
				transaction.execute("ALTER TABLE npcs ADD COLUMN (image VARCHAR(255));", null);
			}

			if (!transaction.doesColumnExist("character_stats", "lastseen")) {
				transaction.execute("ALTER TABLE character_stats ADD COLUMN (lastseen TIMESTAMP);", null);
			}

			if (!transaction.doesColumnExist("postman", "messagetype")) {
				transaction.execute("ALTER TABLE postman ADD COLUMN (messagetype CHAR(1));", null);
			}
			
			if (!transaction.doesColumnExist("achievement", "base_score")) {
				transaction.execute("ALTER TABLE achievement ADD COLUMN (base_score INTEGER);", null);
			}
			
			if(transaction.doesColumnExist("achievement", "description")) {
				if(transaction.getColumnLength("achievement", "description") < 254) {
					transaction.execute("CREATE TABLE tmp_achievement_description (id INTEGER, description VARCHAR(254));", null);
					transaction.execute("INSERT INTO tmp_achievement_description (id, description) SELECT id, description FROM achievement;", null);
					transaction.execute("ALTER TABLE achievement DROP description", null);
					transaction.execute("ALTER TABLE achievement ADD description VARCHAR(254);", null);
					transaction.execute("UPDATE achievement SET description=(SELECT tmp_achievement_description.description FROM tmp_achievement_description WHERE achievement.id=tmp_achievement_description.id);", null);
					transaction.execute("DROPT TABLE tmp_achievement_description;", null);
				}
			}

			if (!transaction.doesColumnExist("character_stats", "finger")) {
				transaction.execute("ALTER TABLE character_stats ADD COLUMN (finger VARCHAR(32));", null);
			}
			
			
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}

		DAORegister.get().register(CharacterDAO.class, new StendhalCharacterDAO());
		DAORegister.get().register(CidDAO.class, new CidDAO());
		DAORegister.get().register(PostmanDAO.class, new PostmanDAO());
		DAORegister.get().register(StendhalBuddyDAO.class, new StendhalBuddyDAO());
		DAORegister.get().register(StendhalHallOfFameDAO.class, new StendhalHallOfFameDAO());
		DAORegister.get().register(StendhalKillLogDAO.class, new StendhalKillLogDAO ());
		DAORegister.get().register(StendhalNPCDAO.class, new StendhalNPCDAO());
		DAORegister.get().register(StendhalWebsiteDAO.class, new StendhalWebsiteDAO());
		DAORegister.get().register(AchievementDAO.class, new AchievementDAO());
	}
}
