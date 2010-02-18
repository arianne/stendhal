package games.stendhal.tools.loganalyser.itemlog.contraband;

import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntry;
import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntryIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

import org.apache.log4j.Logger;

/**
 * Analyses the itemlog for contraband.
 *
 * @author hendrik
 */
public class Analyser {
	private static Logger logger = Logger.getLogger(Analyser.class);
	private static final String SQL = "SELECT timedate, itemid, source, "
		+ "event, param1, param2, param3, param4 FROM itemlog_analyse "
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
			ItemInfo itemInfo = null;
			while (itr.hasNext()) {
				final LogEntry entry = itr.next();

				// detect group change (next item)
				if (!entry.getItemid().equals(itemid)) {
					itemInfo = new ItemInfo();
					itemInfo.setItemid(entry.getItemid());
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
		String timedate = "2010-02-01";
		if (args.length > 0) {
			timedate = args[0];
		}
		final Analyser analyser = new Analyser();
		analyser.analyse(timedate);
	}

}
