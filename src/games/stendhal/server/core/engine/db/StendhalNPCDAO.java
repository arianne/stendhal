/***************************************************************************
 *                    (C) Copyright 2003-2009 - Stendhal                   *
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
import java.sql.SQLException;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

/**
 * database base access for the NPC dump used on the website
 *
 * @author hendrik
 */
public class StendhalNPCDAO {
	private static Logger logger = Logger.getLogger(StendhalNPCDAO.class);


	/**
	 * dumps the properties of the specified SpeakerNPC into the prepared statement as an operation in a batch.
	 *
	 * @param stmt PreparedStatement in batch mode
	 * @param npc  SpeakerNPC
	 * @throws SQLException in case a database error is thrown.
	 */
	private void dumpNPC(PreparedStatement stmt, SpeakerNPC npc) throws SQLException {
		stmt.setString(1, npc.getName());
		stmt.setString(2, npc.getTitle());
		stmt.setString(3, npc.get("class"));
		stmt.setString(4, getOutfit(npc));
		stmt.setInt(5, npc.getHP());
		stmt.setInt(6, npc.getBaseHP());
		stmt.setString(7, npc.getZone().getName());
		stmt.setInt(8, npc.getX());
		stmt.setInt(9, npc.getY());
		stmt.setInt(10, npc.getLevel());
		stmt.setString(11, npc.getDescription());
		stmt.setString(12, npc.getJob());
		stmt.setString(13, npc.getAlternativeImage());
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
	 * dumps all NPCs
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpNPCs(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		transaction.execute("DELETE FROM npcs", null);
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO npcs " +
			"(name, title, class, outfit, hp, base_hp, zone, x, y, level, description, job, image)" +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			dumpNPC(stmt, npc);
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of NPCs in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	/**
	 * dumps all NPCs
	 */
	public void dumpNPCs() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			dumpNPCs(transaction);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}
	}
}
