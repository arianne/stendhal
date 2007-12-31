package games.stendhal.tools.itemlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


/**
 * iterates over the log entries returned by a database query
 *
 * @author hendrik
 */
public class LogEntryIterator extends ResultSetIterator<LogEntry> {
	private static Logger logger = Logger.getLogger(LogEntryIterator.class);

	/**
	 * creates a new LogEntryIterator
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public LogEntryIterator(Statement statement, ResultSet resultSet) {
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
		} catch (SQLException e) {
			logger.error(e, e);
			return null;
		}
    }

}
