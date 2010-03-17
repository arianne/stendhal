package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.db.CidDAO;
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

			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}

		DAORegister.get().register(CharacterDAO.class, new StendhalCharacterDAO());
		DAORegister.get().register(CidDAO.class, new CidDAO());
		DAORegister.get().register(StendhalHallOfFameDAO.class, new StendhalHallOfFameDAO());
		DAORegister.get().register(StendhalKillLogDAO.class, new StendhalKillLogDAO ());
		DAORegister.get().register(StendhalNPCDAO.class, new StendhalNPCDAO());
		DAORegister.get().register(StendhalWebsiteDAO.class, new StendhalWebsiteDAO());
	}
}
