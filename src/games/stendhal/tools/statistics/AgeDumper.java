package games.stendhal.tools.statistics;

import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.core.engine.StendhalRPWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.Transaction;

/**
 * Dumps the Age and Release of players.
 * 
 * @author hendrik
 */
public final class AgeDumper {
	StendhalPlayerDatabase db;

	Transaction trans;

	PreparedStatement ps;

	java.sql.Date date;

	/**
	 * Creates a new AgeDumper.
	 * 
	 * @param db
	 *            JDBCPlayerDatabase
	 */
	private AgeDumper(StendhalPlayerDatabase db) {
		this.db = db;
		this.trans = db.getTransaction();
	}

	/**
	 * Dumps the items.
	 * 
	 * @throws Exception
	 *             in case of an unexpected Exception
	 */
	private void dump() throws Exception {
		String query = "insert into age(datewhen, charname, age, version) values(?, ?, ?, ?)";
		date = new java.sql.Date(new java.util.Date().getTime());
		Connection connection =  trans.getConnection();
		ps = connection.prepareStatement(query);

		for (RPObject object : db) {
			String name = object.get("name");
			// System.out.println(id + " " + name);
			logPlayer(name, object);
		}

		ps.close();
		trans.commit();
	}

	/**
	 * Logs a player.
	 * 
	 * @param name
	 *            character name
	 * @param object
	 *            RPObject
	 * @throws SQLException
	 *             in case of a database error
	 */
	private void logPlayer(String name, RPObject object) throws SQLException {
		int age = -1;
		String release = "0.0";
		if (object.has("age")) {
			age = object.getInt("age");
		}
		if (object.has("release")) {
			release = object.get("release");
		}

		ps.setDate(1, date);
		ps.setString(2, name);
		ps.setInt(3, age);
		ps.setString(4, release);
		ps.executeUpdate();
	}

	/**
	 * Starts the ItemDumper.
	 * 
	 * @param args
	 *            ignored
	 * @throws Exception
	 *             in case of an unexpected item
	 */
	public static void main(String[] args) throws Exception {
		StendhalRPWorld.get();
		Configuration.setConfigurationFile("marauroa-prod.ini");
		StendhalPlayerDatabase db = (StendhalPlayerDatabase) StendhalPlayerDatabase.newConnection();
		AgeDumper itemDumper = new AgeDumper(db);
		itemDumper.dump();
	}
}
