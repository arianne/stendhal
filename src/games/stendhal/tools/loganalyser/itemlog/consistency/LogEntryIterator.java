package games.stendhal.tools.loganalyser.itemlog.consistency;

import games.stendhal.tools.loganalyser.util.ResultSetIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


/**
 * Iterates over the log entries returned by a database query.
 *
 * @author hendrik
 */
public class LogEntryIterator extends ResultSetIterator<LogEntry> {
	private static Logger logger = Logger.getLogger(LogEntryIterator.class);

	/**
	 * Creates a new LogEntryIterator.
	 *
	 * @param resultSet resultSet
	 */
	public LogEntryIterator(final ResultSet resultSet) {
	    super(resultSet);
    }

	/**
	 * Creates a new LogEntryIterator.
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public LogEntryIterator(final Statement statement, final ResultSet resultSet) {
	    super(statement, resultSet);
    }

	@Override
    protected LogEntry createObject() {
		try {
			return new LogEntry(
				resultSet.getString("timedate"),
				resultSet.getString("itemid"),
	    		resultSet.getString("source"),
	    		resultSet.getString("event"),
	    		resultSet.getString("param1"),
	    		resultSet.getString("param2"),
	    		resultSet.getString("param3"),
	    		resultSet.getString("param4"));
		} catch (final SQLException e) {
			logger.error(e, e);
			return null;
		}
    }

}
