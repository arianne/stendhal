package games.stendhal.tools.copytable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

public class ReadTable {

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		new DatabaseFactory().initializeDatabase();
		TransactionPool transactionPool = TransactionPool.get();

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line = br.readLine();
		int i = 0;
		while (line != null) {
			System.out.println("> " + i);
			DBTransaction transaction = transactionPool.beginWork();
			transaction.execute(line, null);
			transactionPool.commit(transaction);
			System.out.println("< " + i);
			Thread.sleep(3000);
			line = br.readLine();
			i++;
		}
	}
}
