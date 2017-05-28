/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.statistics;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.db.CharacterIterator;
import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

/**
 * Dumps the Age and Release of players.
 *
 * @author hendrik
 */
public final class AgeDumper {
	private static Logger logger = Logger.getLogger(AgeDumper.class);
	java.sql.Date date;

	/**
	 * Dumps the items.
	 *
	 * @param transaction
	 * @throws Exception
	 *             in case of an unexpected Exception
	 */
	private void dump(DBTransaction transaction) throws Exception {
		final String query = "insert into age(datewhen, charname, age, version) values(?, ?, ?, ?)";
		date = new java.sql.Date(new java.util.Date().getTime());
		PreparedStatement ps = transaction.prepareStatement(query, null);

		for (final RPObject object : new CharacterIterator(transaction, false)) {
			final String name = object.get("name");
			// System.out.println(id + " " + name);
			logPlayer(ps, name, object);
		}

		ps.close();
	}

	/**
	 * Logs a player.
	 *
	 * @param ps
	 * @param name
	 *            character name
	 * @param object
	 *            RPObject
	 * @throws SQLException
	 *             in case of a database error
	 */
	private void logPlayer(final PreparedStatement ps, final String name, final RPObject object) throws SQLException {
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
	 * Starts the ItemDumper.
	 *
	 * @param args
	 *            ignored
	 * @throws Exception
	 *             in case of an unexpected item
	 */
	public static void main(final String[] args) throws Exception {
		new DatabaseFactory().initializeDatabase();
		SingletonRepository.getRPWorld();
		Configuration.setConfigurationFile("marauroa-prod.ini");
		final AgeDumper itemDumper = new AgeDumper();
		itemDumper.dump();
	}
}
