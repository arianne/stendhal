package games.stendhal.server.core.engine.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import marauroa.common.game.IRPZone;
import marauroa.server.db.DBTransaction;

public class StendhalRPZoneDAO {
	private static Logger logger = Logger.getLogger(StendhalRPZoneDAO.class);

	/**
	 * dumps the properties of the specified zone into the prepared statement as an operation in a batch.
	 *
	 * @param stmt PreparedStatement in batch mode
	 * @param zone StendhalRPZone
	 * @throws SQLException in case a database error is thrown.
	 */
	private void dumpZone(PreparedStatement stmt, StendhalRPZone zone) throws SQLException {
		zone.calculateDangerLevel();
		stmt.setString(1, zone.getName());
		stmt.setInt(2, zone.getLevel());
		stmt.setInt(3, zone.isInterior() ? 1 : 0);
		stmt.setInt(4, zone.getX());
		stmt.setInt(5, zone.getY());
		stmt.setInt(6, zone.getHeight());
		stmt.setInt(7, zone.getWidth());
		stmt.setInt(8, zone.isPublicAccessible() ? 1 : 0);
		stmt.setString(9, zone.getAttributes().get("readable_name"));
		stmt.setString(10, zone.describe());
		stmt.setString(11, zone.getAttributes().get("color_method"));
		stmt.setString(12, zone.getAttributes().get("color"));
		stmt.setString(13, zone.getAttributes().get("blend_method"));
		stmt.setDouble(14, Double.parseDouble(zone.getAttributes().get("danger_level")));
		stmt.setString(15, zone.getAttributes().get("weather"));
		stmt.addBatch();
	}

	/**
	 * dumps all zones
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpZones(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		transaction.execute("DELETE FROM zoneinfo", null);
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO zoneinfo " +
			"(name, level, iterior, x, y, height, width, accessable, readableName, description, colorMethod, color, blendMethod, dangerLevel, weather)" +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		for (IRPZone zone : StendhalRPWorld.get()) {
			dumpZone(stmt, (StendhalRPZone) zone);
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of zones in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

}
