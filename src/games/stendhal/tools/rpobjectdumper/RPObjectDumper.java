package games.stendhal.tools.rpobjectdumper;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.RPObjectDAO;

/**
 * dumps an rpobject
 *
 * @author hendrik
 */
public class RPObjectDumper {

	public static void main(String[] args) throws SQLException, IOException {
		new DatabaseFactory().initializeDatabase();
		int objectid = Integer.parseInt(args[0]);
		RPObject object = DAORegister.get().get(RPObjectDAO.class).loadRPObject(objectid);
		System.out.println(object);
	}
}
