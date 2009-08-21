package games.stendhal.tools.copytable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

import org.apache.log4j.Logger;

public class ReadTable {
	private static Logger logger = Logger.getLogger(ReadTable.class);

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		new DatabaseFactory().initializeDatabase();
		TransactionPool transactionPool = TransactionPool.get();

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line = br.readLine();
		int i = 0;
		StringBuilder cmd = new StringBuilder();
		while (line != null) {
			System.out.println("> " + i);
			if (line.startsWith("--")) {
				line = br.readLine();
				i++;
				continue;
			}
			cmd.append(" " + line);
			
			if (cmd.indexOf(";") > -1) {
				DBTransaction transaction = transactionPool.beginWork();
				try {
					if (cmd.indexOf("DROP TABLE") != 0 && cmd.indexOf("CREATE TABLE") != 0) {
						transaction.execute(line, null);
					}
				} catch (SQLException e) {
					logger.error(cmd, e);
				}
				transactionPool.commit(transaction);
				cmd = new StringBuilder();
				Thread.sleep(3000);
			}

			System.out.println("< " + i);
			line = br.readLine();
			i++;
		}
	}
}
