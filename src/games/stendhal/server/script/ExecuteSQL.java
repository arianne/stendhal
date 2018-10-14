/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;


/**
 * Executes an sql statement. WARNING: This is a quick hack: The sql statement and the post processing is hard coding and it blocks the server creating lag.
 *
 * @author hendrik
 */
public class ExecuteSQL extends ScriptImpl {
	private static Logger logger = Logger.getLogger(ExecuteSQL.class);

	@Override
	public void execute(Player admin, List<String> args) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			try {
				if (!transaction.doesColumnExist(args.get(0), args.get(1))) {
					admin.sendPrivateText("Column " + args.get(1) + " does not exist");
				}

				String sql = "SELECT level, " + args.get(1) + " FROM character_stats, characters, account WHERE admin<600 AND charname=name AND player_id=account.id AND account.status='active' ORDER BY 1, 2";

				Object lastGroup = null;
				ArrayList<Integer> data = new ArrayList<Integer>();
				ResultSet results = transaction.query(sql, null);
				while (results.next()) {
					Object group = results.getObject(1);
					if (!group.equals(lastGroup)) {
						printMedian(lastGroup, data);
						lastGroup = group;
						data.clear();
					}
					data.add(Integer.valueOf(results.getInt(2)));
				}
				printMedian(lastGroup, data);
			} catch (Exception e) {
				logger.error(e, e);
			}
		} finally {
			try {
				TransactionPool.get().commit(transaction);
			} catch (SQLException e) {
				logger.error(e, e);
			}
		}

	}

	private void printMedian(Object lastGroup, ArrayList<Integer> data) {
		//System.out.println("-----------" + lastGroup + " " + data.size() + " " + data);
		int cnt = data.size();
		if (cnt >= 2) {
			int median = -1;
			if (cnt % 2 == 0) {
				median = (data.get(cnt / 2 - 1).intValue() + data.get(cnt / 2).intValue()) / 2;
			} else {
				median = data.get(cnt / 2).intValue();
			}
			logger.info("median: " + lastGroup + ", " + median);
		}
	}
}
