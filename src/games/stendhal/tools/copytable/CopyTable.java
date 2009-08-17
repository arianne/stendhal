package games.stendhal.tools.copytable;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

public class CopyTable {

	public static void main(String[] args) throws SQLException {
		new DatabaseFactory().initializeDatabase();
		TransactionPool transactionPool = TransactionPool.get();
		
		for (int i = 0; i < 34600; i++) {
			System.out.println(i);
			String cmd = "INSERT INTO itemlog_new (id, timedate, itemid, source, event, param1, param2, param3, param4)"
				+ " SELECT id, timedate, itemid, source, event, param1, param2, param3, param4 FROM itemlog WHERE id >= " + (i * 1000) + " AND id < "  + ((i+1) * 1000);
			DBTransaction transaction = transactionPool.beginWork();
			transaction.execute(cmd, null);
			transactionPool.commit(transaction);
			System.out.println(i);
		}
	}
}
