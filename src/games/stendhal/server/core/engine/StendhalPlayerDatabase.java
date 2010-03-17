package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.db.CidDAO;
import games.stendhal.server.core.engine.db.StendhalCharacterDAO;
import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.db.StendhalNPCDAO;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.JDBCSQLHelper;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

public class StendhalPlayerDatabase {

	private static boolean shouldStop;
	
	private static final Logger logger = Logger.getLogger(StendhalPlayerDatabase.class);
	private static TimerTask task = new TimerTask() {

		ItemLogger itemLogger = new ItemLogger();

		@Override
		public void run() {
			
			try {
				processGameEvents();
				itemLogger.processEntries();
			} catch (SQLException e) {
				logger.error(e, e);
			}
			
			if (shouldStop && GameEventQueue.queue.isEmpty()) {
				this.cancel();
			}
		}
		
		private void processGameEvents() throws SQLException {
			final DBTransaction transaction = TransactionPool.get().beginWork();
			try {
				for (GameEvent current = GameEventQueue.getGameEvents().poll();
					current != null; current = GameEventQueue.getGameEvents().poll()) {
					// DAORegister.get().get(GameEventDAO.class).addGameEvent(transaction, current.source, current.event, current.params);
					if ("server system".equals(current.source) && "shutdown".equals(current.event)) {
						shouldStop = true;
					}
				}
				TransactionPool.get().commit(transaction);
			} catch (SQLException e) {
				logger.error(e, e);
				TransactionPool.get().rollback(transaction);
			}
		}

	};

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

		new Timer().schedule(task , 2000, 300);
	}
}
