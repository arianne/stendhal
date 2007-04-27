package games.stendhal.tools.statistics;

import games.stendhal.server.StendhalPlayerDatabase;
import games.stendhal.server.StendhalRPWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.GenericDatabaseException;
import marauroa.server.game.JDBCPlayerDatabase;
import marauroa.server.game.JDBCTransaction;
import marauroa.server.game.Transaction;

/**
 * Dumps the Age and Release of players
 *
 * @author hendrik
 */
public class AgeDumper {
	JDBCPlayerDatabase db;
	Transaction trans;
	PreparedStatement ps;
	java.sql.Date date;

	/**
	 * Creates a new AgeDumper
	 *
	 * @param db JDBCPlayerDatabase
	 * @throws GenericDatabaseException if no database connection can be created
	 */
	private AgeDumper(JDBCPlayerDatabase db) throws GenericDatabaseException {
		this.db = db;
		this.trans = db.getTransaction();
	}

	/**
	 * dumps the items
	 *
	 * @throws Exception in case of an unexspected Exception
	 */
	private void dump() throws Exception {
		JDBCPlayerDatabase.RPObjectIterator it = db.iterator(trans);
		String query = "insert into age(datewhen, charname, age, version) values(?, ?, ?, ?)";
	    date = new java.sql.Date(new java.util.Date().getTime());
		Connection connection = ((JDBCTransaction) trans).getConnection();
		ps = connection.prepareStatement(query);

		while (it.hasNext()) {
			int id = it.next();
			RPObject object = db.loadRPObject(trans, id);
			String name = object.get("name");
			//System.out.println(id + " " + name);
			logPlayer(name, object);
		}
		ps.close();
		trans.commit();
	}

	/**
	 * logs a player
	 *
	 * @param name     character name
	 * @param object   RPObject
	 * @throws SQLException in case of a database error
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
	 * starts the ItemDumper
	 *
	 * @param args ignored
	 * @throws Exception in case of an unexspected item
	 */
	public static void main(String[] args) throws Exception {
		StendhalRPWorld.get();
		Configuration.setConfigurationFile("marauroa-prod.ini");
		JDBCPlayerDatabase db = (JDBCPlayerDatabase) StendhalPlayerDatabase.resetDatabaseConnection();
		AgeDumper itemDumper = new AgeDumper(db);
		itemDumper.dump();
	}
}
