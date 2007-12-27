package games.stendhal.server.core.engine;

import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
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

	private static String getQuantity(RPObject item) {
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		return Integer.toString(quantity);
	}

	public static void loadOnLogin(Player player, RPSlot slot, Item item) {
		if (item.has(ATTR_LOGID)) {
			return;
		}
		log(item, player, "create", item.get("name"), getQuantity(item), "olditem", slot.getName());
	}

	public static void destroyOnLogin(Player player, RPSlot slot, RPObject item) {
		log(item, player, "destroy", item.get("name"), getQuantity(item), "on login", slot.getName());
    }

	public static void dropQuest(Player player, Item item) {
		log(item, player, "destroy", item.get("name"), getQuantity(item), "quest", null);
    }

	public static void timeout(Item item) {
		log(item, null, "destroy", item.get("name"), getQuantity(item), "timeout", item.getZone().getID().getID() + " " + item.getX() + " " + item.getY());
    }

	public static void displace(Player player, PassiveEntity item, StendhalRPZone zone, int x, int y) {
		log(item, player, "ground-to-ground", zone.getID().getID(), item.getX() + " " + item.getY(), zone.getID().getID(), x + " " + y);
    }

	public static void equipAction(Player player, Item item, String[] sourceInfo, String[] destInfo) {
	    log(item, player, sourceInfo[0] + "-to-" + destInfo[0], sourceInfo[1], sourceInfo[2], destInfo[1], destInfo[2]);
    }

	public static void merge(Player player, Item oldItem, Item outlivingItem) {
		assignIDIfNotPresent(oldItem, outlivingItem);
		String oldQuantity = getQuantity(oldItem);
		String oldOutlivingQuantity = getQuantity(outlivingItem);
		String newQuantity = Integer.toString(Integer.parseInt(oldQuantity) + Integer.parseInt(oldOutlivingQuantity));
	    log(oldItem, player, "merge in", outlivingItem.get(ATTR_LOGID), oldQuantity, oldOutlivingQuantity, newQuantity);
	    log(outlivingItem, player, "merged in", oldItem.get(ATTR_LOGID), oldOutlivingQuantity, oldQuantity, newQuantity);
    }

	public static void splitOff(Player player, Item item, StackableItem newItem, int quantity) {
		assignIDIfNotPresent(item, newItem);
		String outlivingQuantity = getQuantity(item);
		String newQuantity = getQuantity(newItem);
		String oldQuantity = Integer.toString(Integer.parseInt(outlivingQuantity) + Integer.parseInt(newQuantity));
	    log(item, player, "split out", newItem.get(ATTR_LOGID), oldQuantity, outlivingQuantity, newQuantity);
	    log(newItem, player, "splitted out", item.get(ATTR_LOGID), oldQuantity, newQuantity, outlivingQuantity);
    }

	/*
	create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity / olditem
	slot-to-slot       source       source-slot       target    target-slot
	ground-to-slot     zone         x         y       target    target-slot
	slot-to-ground     source       source-slot       zone         x         y
	ground-to-ground   zone         x         y       zone         x         y
	use                old-quantity new-quantity
	destroy            name         quantity          by admin / by quest / on login / timeout on ground
	merge in           outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
	merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
	split out          new_id            old-quantity         outliving-quantity       new-quantity
	splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity

	the last two are redundant pairs to simplify queries
	 */

	private static void log(RPObject item, Player player, String event, String param1, String param2, String param3, String param4) {
		JDBCTransaction transaction = (JDBCTransaction) JDBCDatabase.getDatabase().getTransaction();
		try {

			assignIDIfNotPresent(transaction, item);
			writeLog(transaction, item, player, event, param1, param2, param3, param4);

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
	 * @param item item
	 * @throws SQLException in case of a database error
	 */
	private static void assignIDIfNotPresent(RPObject... items) {
		JDBCTransaction transaction = (JDBCTransaction) JDBCDatabase.getDatabase().getTransaction();
		try {
			for (RPObject item : items) {
				assignIDIfNotPresent(transaction, item);
			}

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
	private static void assignIDIfNotPresent(JDBCTransaction transaction, RPObject item) throws SQLException {
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

	private static void writeLog(JDBCTransaction transaction, RPObject item, Player player, String event, String param1, String param2, String param3, String param4) throws SQLException {
		String playerName = null;
		if (player != null) {
			playerName = player.getName();
		}
		String query = "INSERT INTO itemlog (itemid, source, event, " +
			"param1, param2, param3, param4) VALUES (" + 
			item.getInt(ATTR_LOGID) + ", '" + 
			StringChecker.trimAndEscapeSQLString(playerName, 64) + "', '" +
			StringChecker.trimAndEscapeSQLString(event, 64) + "', '" +
			StringChecker.trimAndEscapeSQLString(param1, 64) + "', '" +
			StringChecker.trimAndEscapeSQLString(param2, 64) + "', '" +
			StringChecker.trimAndEscapeSQLString(param3, 64) + "', '" +
			StringChecker.trimAndEscapeSQLString(param4, 64) + "');";

		transaction.getAccessor().execute(query);
	}

}
