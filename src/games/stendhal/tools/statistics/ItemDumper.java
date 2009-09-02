package games.stendhal.tools.statistics;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.db.CharacterIterator;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

import org.apache.log4j.Logger;

/**
 * Dumps the items of all players into a table called items.
 * 
 * @author hendrik
 */
public class ItemDumper {
	private static Logger logger = Logger.getLogger(AgeDumper.class);
	java.sql.Date date;

	/**
	 * dumps the items.
	 * 
	 * @throws Exception
	 *             in case of an unexpected Exception
	 */
	private void dump(DBTransaction transaction) throws Exception {
		final String query = "insert into items(datewhen, charname, slotname, itemid, itemname, amount) values(?, ?, ?, ?, ?, ?)";
		date = new java.sql.Date(new java.util.Date().getTime());
		PreparedStatement ps = transaction.prepareStatement(query, null);

		for (final RPObject object : new CharacterIterator(transaction, false)) {
			final String name = object.get("name");
			final int id = object.getInt("id");
			System.out.println(id + " " + name);
			for (final RPSlot slot : object.slots()) {
				final String slotName = slot.getName();
				for (final RPObject item : slot) {
					if (item.has("type") && item.get("type").equals("item")) {
						logItem(ps, name, slotName, item);
					}
				}
			}
		}
		ps.close();
	}

	/**
	 * logs an item.
	 * 
	 * @param name
	 *            character name
	 * @param slotName
	 *            slot name
	 * @param item
	 *            item name
	 * @throws SQLException
	 *             in case of a database error
	 */
	private void logItem(final PreparedStatement ps, final String name, final String slotName, final RPObject item)
			throws SQLException {
		final String itemName = item.get("name");
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		int itemid = -1;
		if (item.has("itemid")) {
			itemid = item.getInt("itemid");
		}
		ps.setDate(1, date);
		ps.setString(2, name);
		ps.setString(3, slotName);
		ps.setInt(4, itemid);
		ps.setString(5, itemName);
		ps.setInt(6, quantity);
		ps.executeUpdate();
	}

	
	public void dump() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			dump(transaction);
			TransactionPool.get().commit(transaction);
		} catch (Exception e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}
	}

	/**
	 * starts the ItemDumper.
	 * 
	 * @param args
	 *            ignored
	 * @throws Exception
	 *             in case of an unexpected item
	 */
	public static void main(final String[] args) throws Exception {
		new DatabaseFactory().initializeDatabase();	
		SingletonRepository.getRPWorld();
		//Configuration.setConfigurationFile("marauroa-prod.ini");
		final ItemDumper itemDumper = new ItemDumper();
		itemDumper.dump();
	}
}
