/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
		stmt.setInt(1, 1);
		stmt.setString(2, zone.getName());
		stmt.setInt(3, zone.getLevel());
		stmt.setInt(4, zone.isInterior() ? 1 : 0);
		stmt.setInt(5, zone.getX());
		stmt.setInt(6, zone.getY());
		stmt.setInt(7, zone.getHeight());
		stmt.setInt(8, zone.getWidth());
		stmt.setInt(9, zone.isPublicAccessible() ? 1 : 0);
		stmt.setString(10, zone.getAttributes().get("readable_name"));
		stmt.setString(11, zone.describe());
		stmt.setString(12, zone.getAttributes().get("color_method"));
		stmt.setString(13, zone.getAttributes().get("color"));
		stmt.setString(14, zone.getAttributes().get("blend_method"));
		stmt.setDouble(15, Double.parseDouble(zone.getAttributes().get("danger_level")));
		stmt.setString(16, zone.getAttributes().get("weather"));
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
		transaction.execute("UPDATE zoneinfo SET active=0", null);
		PreparedStatement stmt = transaction.prepareStatement("UPDATE zoneinfo SET "
				+ "active=?, name=?, level=?, iterior=?, x=?, y=?, height=?, width=?, accessable=?, readableName=?, description=?, "
				+ "colorMethod=?, color=?, blendMethod=?, dangerLevel=?, weather=? "
				+ "WHERE name=?;", null);

		Map<String, IRPZone> unknown = new HashMap<>();
		for (IRPZone iZone : StendhalRPWorld.get()) {
			StendhalRPZone zone = (StendhalRPZone) iZone;
			unknown.put(zone.getName().trim(), zone);
			stmt.setString(17, zone.getName());
			dumpZone(stmt, zone);
		}
		stmt.executeBatch();


		// add new
		ResultSet resultSet = transaction.query("SELECT name FROM zoneinfo", null);
		while (resultSet.next()) {
			unknown.remove(resultSet.getString(1));
		}

		stmt = transaction.prepareStatement("INSERT INTO zoneinfo " +
			"(active, name, level, iterior, x, y, height, width, accessable, readableName, description, colorMethod, color, blendMethod, dangerLevel, weather)" +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		for (IRPZone zone : unknown.values()) {
			dumpZone(stmt, (StendhalRPZone) zone);
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of zones in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

}
