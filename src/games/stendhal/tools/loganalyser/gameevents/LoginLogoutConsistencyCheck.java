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
package games.stendhal.tools.loganalyser.gameevents;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

/**
 * Analyses login/logout events for suspicious activity.
 *
 * @author hendrik
 */
public class LoginLogoutConsistencyCheck {
	private static Logger logger = Logger.getLogger(LoginLogoutConsistencyCheck.class);
	private static final String SQL = "SELECT id, timedate, source, "
		+ "event, param1, param2 FROM gameEvents USE INDEX (i_gameEvents_timedate)  "
		+ " WHERE (event='login' OR event='logout') AND timedate > '[timedate]'"
		+ " ORDER BY timedate";

	private Set<String> online = new HashSet<String>();

	private GameEventEntryIterator queryDatabase(final DBTransaction transaction, final String timedate) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("timedate", timedate);
		final ResultSet resultSet = transaction.query(SQL, params);
		return new GameEventEntryIterator(resultSet);
	}

	/**
	 * analyses the log
	 *
	 * @param timedate date when to start
	 * @return <code>false</code> if there were problems, <code>true</code>
	 * 	otherwise
	 */
	public boolean analyse(final String timedate) {
		boolean okay = true;
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			final Iterator<GameEventEntry> itr = queryDatabase(transaction, timedate);
			while (itr.hasNext()) {
				final GameEventEntry entry = itr.next();
				if (entry.getEvent().equals("login")) {
					online.add(entry.getSource());
				} else if (entry.getEvent().equals("logout")) {
					if (!online.contains(entry.getSource())) {
						okay = false;
						System.out.println(entry);
					}
					online.remove(entry.getSource());
				}
			}
			TransactionPool.get().commit(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			logger.error(e, e);
		}
		return okay;
	}

	/**
	 * Entry point.
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		String timedate = "1900-01-01";
		if (args.length > 0) {
			timedate = args[0];
		}
		final LoginLogoutConsistencyCheck analyser = new LoginLogoutConsistencyCheck();
		boolean res = analyser.analyse(timedate);
		if (res) {
			System.exit(1);
		}
	}

}
