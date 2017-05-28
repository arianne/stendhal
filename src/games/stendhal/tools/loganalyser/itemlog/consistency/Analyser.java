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
package games.stendhal.tools.loganalyser.itemlog.consistency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

/**
 * Analyses the itemlog for suspicious activity.
 *
 * @author hendrik
 */
public class Analyser {
	private static Logger logger = Logger.getLogger(Analyser.class);
	private static final String SQL = "SELECT id, timedate, itemid, source, "
		+ "event, param1, param2, param3, param4 FROM itemlog "
		+ " WHERE timedate > '[timedate]'"
		+ " ORDER BY itemid, timedate";

	private LogEntryIterator queryDatabase(final DBTransaction transaction, final String timedate) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("timedate", timedate);
		final ResultSet resultSet = transaction.query(SQL, params);
		return new LogEntryIterator(resultSet);
	}

	public void analyse(final String timedate) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			final Iterator<LogEntry> itr = queryDatabase(transaction, timedate);
			String itemid = "-1";
			ItemLocation itemLocation = null;
			while (itr.hasNext()) {
				final LogEntry entry = itr.next();

				// detect group change (next item)
				if (!entry.getItemid().equals(itemid)) {
					itemLocation = new ItemLocation();
					itemid = entry.getItemid();
				}

				if (itemLocation != null) {
					// check consistency
					final boolean res = itemLocation.check(entry.getEvent(), entry.getParam1(), entry.getParam2());
					if (!res) {
						logger.error("Inconsistency: expected location \t" + itemLocation + "\t but log entry said \t" + entry);
					}

					// update item location
					itemLocation.update(entry.getEvent(), entry.getParam3(), entry.getParam4());
				}
			}
			TransactionPool.get().commit(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			logger.error(e, e);
		}
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
		final Analyser analyser = new Analyser();
		analyser.analyse(timedate);
	}

}
