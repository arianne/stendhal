package games.stendhal.server.core.engine;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

/**
 * Item Logger
 *
 * @author hendrik
 */
public class ItemLogger {

	private static Logger logger = Logger.getLogger(ItemLogger.class);

	/*
	CREATE TABLE itemid (
	  last_id INTEGER
	);
	
	CREATE TABLE itemlog (
	  id         SERIAL,
	  itemid     INTEGER,
	  timedate   TIMESTAMP,
	  source     VARCHAR(20),
	  event,     VARCHAR(20),
	  param1     VARCHAR(50),
	  param2     VARCHAR(50),
	  param3     VARCHAR(50),
	  param4     VARCHAR(50)
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
		Transaction transaction = JDBCDatabase.getDatabase().getTransaction();
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

	private static void assignIDIfNotPresent(Transaction transaction, Item item) {
		// TODO Auto-generated method stub
		
	}

	private static void log(Transaction transaction, Item item, Player player, String event, String param1, String param2, String param3, String param4) {
		// TODO Auto-generated method stub
		
	}
}
