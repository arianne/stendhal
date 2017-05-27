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
package games.stendhal.tools.copytable;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

public class CopyTable {

	public static void main(String[] args) throws SQLException, InterruptedException {
		new DatabaseFactory().initializeDatabase();
		TransactionPool transactionPool = TransactionPool.get();

		for (int i = 1122; i < 346000; i++) {
			System.out.println("> " + i);
			String cmd = "INSERT INTO itemlog_new (id, timedate, itemid, source, event, param1, param2, param3, param4)"
				+ " SELECT id, timedate, itemid, source, event, param1, param2, param3, param4 FROM itemlog WHERE id >= " + (i * 100) + " AND id < "  + ((i+1) * 100);
			DBTransaction transaction = transactionPool.beginWork();
			transaction.execute(cmd, null);
			transactionPool.commit(transaction);
			System.out.println("< " + i);
			Thread.sleep(3000);
		}
	}
}
