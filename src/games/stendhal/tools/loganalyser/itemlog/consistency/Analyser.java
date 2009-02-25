package games.stendhal.tools.loganalyser.itemlog.consistency;

import games.stendhal.server.core.engine.SingletonRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import marauroa.common.Log4J;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

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
	
	private LogEntryIterator queryDatabase(final String timedate) {
		final Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		try {
			final Connection connection = transaction.getConnection();
			final Statement stmt = connection.createStatement();
			final ResultSet resultSet = stmt.executeQuery(String.format(SQL, StringChecker.escapeSQLString(timedate)));
			return new LogEntryIterator(stmt, resultSet);
		
		} catch (final SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (final SQLException e1) {
				logger.error(e1, e1);
			}
		}
		return null;
	}
	
	public void analyse(final String timedate) {
		final Iterator<LogEntry> itr = queryDatabase(timedate);
		String itemid = "-1";
		ItemLocation itemLocation = null;
		while (itr.hasNext()) {
			final LogEntry entry = itr.next();

			// detect group change (next item)
			if (!entry.getItemid().equals(itemid)) {
				itemLocation = new ItemLocation();
				itemid = entry.getItemid();
			}

			// check consistency
			final boolean res = itemLocation.check(entry.getEvent(), entry.getParam1(), entry.getParam2());
			if (!res) {
				logger.error("Inconsistency: exspected location \t" + itemLocation + "\t but log entry said \t" + entry);
			}

			// update item location
			itemLocation.update(entry.getEvent(), entry.getParam3(), entry.getParam4());
		}
	}

	/**
	 * Entry point.
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		Log4J.init();
		String timedate = "1900-01-01";
		if (args.length > 0) {
			timedate = args[0];
		}
		final Analyser analyser = new Analyser();
		analyser.analyse(timedate);
	}

}
