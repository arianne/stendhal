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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;

/**
 * database access to the kill log
 *
 * @author hendrik
 */
public class StendhalKillLogDAO {
	private static Logger logger = Logger.getLogger(StendhalKillLogDAO.class);

	
	/**
	 * Logs a kill.
	 * 
	 * @param transaction transaction
	 * @param killed killed entity
	 * @param killer killer
	 * @throws SQLException in case of an database error
	 */
	public void logKill(final DBTransaction transaction, final Entity killed, final Killer killer) throws SQLException {
		// try update in case we already have this combination
		String query = "UPDATE kills SET cnt = cnt+1"
			+ " WHERE killed = '[killed]' AND killed_type = '[killed_type]'"
			+ " AND killer = '[killer]' AND killer_type = '[killer_type]'"
			+ " AND day = '[day]';";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("killed", killed.getName());
		params.put("killed_type", entityToType(killed));
		params.put("killer", killer.getName());
		params.put("killer_type", entityToType(killer));
		params.put("day", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		final int rowCount = transaction.execute(query, params);

		// in case we did not have this combination yet, make an insert
		if (rowCount == 0) {
			query = "INSERT INTO kills (killed, killed_type, killer, killer_type, day, cnt)"
				+ " VALUES ('[killed]', '[killed_type]', '[killer]', '[killer_type]', '[day]', 1)";
			transaction.execute(query, params);
		}

	}

	/**
	 * Logs a kill.
	 *
	 * @param killed killed entity
	 * @param killer killer
	 */
	public void logKill(final Entity killed, final Entity killer) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			logKill(transaction, killed, killer);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}
	}
	
	/**
	 * Creates a one letter type string based on the class of the entity.
	 *
	 * @param entity Entity
	 * @return P for players, C for creatures, E for other entities
	 */
	public String entityToType(final Killer entity) {
		if (entity instanceof Player) {
			return "P";
		} else if (entity instanceof Creature) {
			return "C";
		} else {
			return "E";
		}
	}

}
