package games.stendhal.tools.itemlog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import marauroa.common.Log4J;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.JDBCTransaction;
import marauroa.server.game.db.StringChecker;

import org.apache.log4j.Logger;

/**
 * Analyses the itemlog for suspicious activity.
 *
 * @author hendrik
 */
public class Analyser {
	private static Logger logger = Logger.getLogger(Analyser.class);
	private static final String SQL = "SELECT timedate, itemid, source, "
		+ "event, param1, param2, param3, param4 FROM itemlog "
		+ " WHERE timedate > '%0$s'"
		+ " ORDER BY itemid, timedate";
	
	private LogEntryIterator queryDatabase(String timedate) {
		JDBCTransaction transaction = (JDBCTransaction) JDBCDatabase.getDatabase().getTransaction();
		try {
			Connection connection = transaction.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(String.format(SQL, StringChecker.escapeSQLString(timedate)));
			return new LogEntryIterator(stmt, resultSet);
		
		} catch (SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (SQLException e1) {
				logger.error(e1, e1);
			}
		}
		return null;
	}
	
	public void analyse(String timedate) {
		Iterator<LogEntry> itr = queryDatabase(timedate);
		String itemid = "-1";
		ItemLocation itemLocation = null;
		while (itr.hasNext()) {
			LogEntry entry = itr.next();

			// detect group change (next item)
			if (!entry.getItemid().equals(itemid)) {
				itemLocation = new ItemLocation();
				itemid = entry.getItemid();
			}

			// check consistency
			boolean res = itemLocation.check(entry.getEvent(), entry.getParam1(), entry.getParam2());
			if (!res) {
				logger.error("Inconsistency: exspected location \t" + itemLocation + "\t but log entry said \t" + entry);
			}

			// update item location
			itemLocation.update(entry.getEvent(), entry.getParam3(), entry.getParam4());
		}
	}

	/**
	 * entry point
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Log4J.init();
		String timedate = "1900-01-01";
		if (args.length > 0) {
			timedate = args[0];
		}
		Analyser analyser = new Analyser();
		analyser.analyse(timedate);
	}

}
