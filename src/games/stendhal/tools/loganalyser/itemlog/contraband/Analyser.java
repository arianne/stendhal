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
package games.stendhal.tools.loganalyser.itemlog.contraband;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;
import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntryIterator;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

/**
 * Analyses the itemlog for contraband.
 *
 * @author hendrik
 */
public class Analyser {
	private static Logger logger = Logger.getLogger(Analyser.class);
	private static final String SQL = "SELECT id, timedate, itemid, source, "
		+ "event, param1, param2, param3, param4 FROM itemlog_analyse "
		+ " WHERE timedate > '[timedate]'"
		+ " ORDER BY itemid, id";

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
			ItemInfo oldItemInfo = new ItemInfo();
			while (itr.hasNext()) {
				final LogEntry entry = itr.next();

				// detect group change (next item)
				if (!entry.getItemid().equals(oldItemInfo.getItemid())) {
					oldItemInfo = new ItemInfo();
					oldItemInfo.setItemid(entry.getItemid());
					oldItemInfo.setName("");
					oldItemInfo.setQuantity("1");
					oldItemInfo.setOwner(entry.getSource());
				}

				ItemInfo itemInfo = (ItemInfo) oldItemInfo.clone();

				itemInfo.setOwner(entry.getSource());
				ItemEventType eventType = ItemEventTypeFactory.create(entry.getEvent());
				eventType.process(entry, itemInfo);

				if (!oldItemInfo.getOwner().equals(itemInfo.getOwner())) {
					logTransfer(oldItemInfo, itemInfo, entry);
				}
/*
| create           |
| destroy          |
| ground-to-ground |
| ground-to-slot   |
| market-to-slot   |
| merge in         |
| merged in        |
| register         |
| slot-to-ground   |
| slot-to-market   |
| slot-to-slot     |
| split out        |
| splitted out     |
*/
				oldItemInfo = itemInfo;
			}
			TransactionPool.get().commit(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			logger.error(e, e);
		}
		System.exit(0);
	}

	private void logTransfer(ItemInfo oldItemInfo, ItemInfo itemInfo, LogEntry entry) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			String query = "INSERT INTO itemtransfer_analyse (itemid, name, quantity, giver, receiver, oldid) VALUES ([itemid], '[name]', '[quantity]', '[giver]', '[receiver]', [oldid]);";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("itemid", oldItemInfo.getItemid());
			params.put("name", oldItemInfo.getName());
			params.put("quantity", itemInfo.getQuantity());
			params.put("giver", oldItemInfo.getOwner());
			params.put("receiver", itemInfo.getOwner());
			params.put("oldid", entry.getId());

			transaction.execute(query, params);
			//System.out.println(itemInfo + " " + oldItemInfo.getOwner() + " " + itemInfo.getOwner());

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
		Configuration.setConfigurationFile("/home/hendrik/workspace/stendhal/server_strato.ini");
		new DatabaseFactory().initializeDatabase();
		String timedate = "2000-01-01";
		if (args.length > 0) {
			timedate = args[0];
		}
		final Analyser analyser = new Analyser();
		analyser.analyse(timedate);
	}

}
