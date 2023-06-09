/***************************************************************************
 *                    (C) Copyright 2003-2023 - Stendhal                   *
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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.server.db.DBTransaction;

/**
 * database base access for the NPC dump used on the website
 *
 * @author hendrik
 */
public class StendhalNPCDAO {
	private static Logger logger = Logger.getLogger(StendhalNPCDAO.class);


	/**
	 * Dumps the properties of the specified SpeakerNPC into the prepared statement as an operation in
	 * a batch.
	 *
	 * @param stmt
	 *   PreparedStatement in batch mode.
	 * @param npc
	 *   SpeakerNPC.
	 * @param shopsInfo
	 *   What the NPC buys & sells.
	 * @throws
	 *   SQLException in case a database error is thrown.
	 */
	private void dumpNPC(PreparedStatement stmt, SpeakerNPC npc) throws SQLException {
		stmt.setInt(1, 1);
		stmt.setString(2, npc.getName());
		stmt.setString(3, npc.getTitle());
		stmt.setString(4, npc.get("class"));
		stmt.setString(5, getOutfit(npc));
		stmt.setString(6, getOutfitLayer(npc));
		stmt.setInt(7, npc.getHP());
		stmt.setInt(8, npc.getBaseHP());
		stmt.setString(9, npc.getZone().getName());
		stmt.setInt(10, npc.getX());
		stmt.setInt(11, npc.getY());
		stmt.setInt(12, npc.getLevel());
		stmt.setString(13, npc.getDescription());
		stmt.setString(14, npc.getJob());
		stmt.setString(15, npc.getAlternativeImage());
		stmt.setString(16, npc.get("cloned"));
		stmt.addBatch();
	}

	/**
	 * gets the outfit code as string
	 *
	 * @param npc SpeakerNPC object
	 * @return outfit code as string or null incase there is not outfit specified
	 */
	private String getOutfit(SpeakerNPC npc) {
		String outfit = null;
		if (npc.getOutfit() != null) {
			outfit = Integer.toString(npc.getOutfit().getCode());
		}
		return outfit;
	}

	/**
	 * gets the outfit code as string
	 *
	 * @param npc SpeakerNPC object
	 * @return outfit code as string or null incase there is not outfit specified
	 */
	private String getOutfitLayer(SpeakerNPC npc) {
		String outfit = null;
		if (npc.getOutfit() != null) {
			outfit = (npc.getOutfit().getData(npc.getOutfitColors()));
		}
		return outfit;
	}

	/**
	 * dumps all NPCs
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpNPCs(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		transaction.execute("UPDATE npcs SET active=0", null);
		PreparedStatement stmt = transaction.prepareStatement("UPDATE npcs SET "
				+ "active=?, name=?, title=?, class=?, outfit=?, outfit_layers=?, hp=?, base_hp=?, zone=?, x=?, y=?, "
				+ "level=?, description=?, job=?, image=?, cloned=? "
				+ "WHERE name=?", null);
		Map<String, SpeakerNPC> unknown = new HashMap<>();
		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			unknown.put(npc.getName().trim(), npc);
			stmt.setString(17, npc.getName());
			dumpNPC(stmt, npc);
		}
		stmt.executeBatch();


		// add new
		ResultSet resultSet = transaction.query("SELECT name FROM npcs", null);
		while (resultSet.next()) {
			unknown.remove(resultSet.getString(1));
		}

		stmt = transaction.prepareStatement("INSERT INTO npcs " +
			"(active, name, title, class, outfit, outfit_layers, hp, base_hp, zone, x, y, " +
			"level, description, job, image, cloned) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		for (SpeakerNPC npc : unknown.values()) {
			dumpNPC(stmt, npc);
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of NPCs in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	public Map<String, Integer> getIdMap(DBTransaction transaction) throws SQLException {
		return transaction.queryAsMap("SELECT name, id FROM npcs", null);
	}
}
