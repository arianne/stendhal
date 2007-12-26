package games.stendhal.server.core.engine;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.JDBCTransaction;
import marauroa.server.game.db.StringChecker;

import org.apache.log4j.Logger;

/**
 * Item Logger
 *
 * @author hendrik
 */
public class ItemLogger {

	private static Logger logger = Logger.getLogger(ItemLogger.class);
	private static final String ATTR_LOGID = "logid";

	/*
	CREATE TABLE itemid (
	  last_id INTEGER
	);
	
	CREATE TABLE itemlog (
	  id         SERIAL,
	  timedate   TIMESTAMP,
	  itemid     INTEGER,
	  source     VARCHAR(64),
	  event,     VARCHAR(64),
	  param1     VARCHAR(64),
	  param2     VARCHAR(64),
	  param3     VARCHAR(64),
	  param4     VARCHAR(64)
	);
	
	
	
	create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity / olditem
	slot-to-slot       source       source-slot       target    target-slot
	ground-to-slot     zone         x         y       target    target-slot
	slot-to-ground     source       source-slot       zone         x         y
	ground-to-ground   zone         x         y       zone         x         y
	use                old-quantity new-quantity
	destroy
	timeout
	merge into         outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
	merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
	split out          new_id            old-quantity         outliving-quantity       new-quantity
	splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity
	
	the last two are redundant pairs to simplify queries
	 */

	private static void log(Item item, Player player, String event, String param1, String param2, String param3, String param4) {
		JDBCTransaction transaction = (JDBCTransaction) JDBCDatabase.getDatabase().getTransaction();
		try {

			assignIDIfNotPresent(transaction, item);
			log(transaction, item, player, event, param1, param2, param3, param4);

			transaction.commit();
		} catch (SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (SQLException e1) {
				logger.error(e1, e1);
			}
		}
	}

	/**
	 * Assigns the next logid to the specified item in case it does not already have one
	 *
	 * @param transaction database transaction
	 * @param item item
	 * @throws SQLException in case of a database error
	 */
	private static void assignIDIfNotPresent(JDBCTransaction transaction, Item item) throws SQLException {
		if (item.has(ATTR_LOGID)) {
			return;
		}

		// increment the last_id value (or initialize it in case that table has 0 rows).
		int count = transaction.getAccessor().execute("UPDATE itemid SET last_id = last_id+1;");
		if (count < 0) {
			logger.error("Unexpected return value of execute method: " + count);
		} else if (count == 0) {
			transaction.getAccessor().execute("INSERT INTO itemid (last_id) VALUES (1);");
		}

		// read last_id from database
		int id = transaction.getAccessor().querySingleCellInt("SELECT last_id FROM itemid");
		item.put(ATTR_LOGID, id);
	}

	private static void log(JDBCTransaction transaction, Item item, Player player, String event, String param1, String param2, String param3, String param4) throws SQLException {
		
	}
}
