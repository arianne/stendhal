package games.stendhal.tools.itemlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * iterates over the log entries returned by a database query
 *
 * @author hendrik
 * @param <T> object type
 */
public class ResultSetIterator<T> implements Iterator<T> {
	private static Logger logger = Logger.getLogger(ResultSetIterator.class);
	
	private ResultSet resultSet;
	private boolean hasNext;
	private boolean nextCalled;

	public boolean hasNext() {
		if (nextCalled) {
			return hasNext;
		}
		nextCalled = true;
		try {
	        hasNext = resultSet.next();
        } catch (SQLException e) {
        	hasNext = false;
        	logger.error(e, e);
        }
		return hasNext;
	}

	public T next() {
		try {
	        hasNext = resultSet.next();
        } catch (SQLException e) {
        	hasNext = false;
        	logger.error(e, e);
        }
		nextCalled = false;
		return null;
	}

	public void remove() {
		try {
	        resultSet.deleteRow();
        } catch (SQLException e) {
        	logger.error(e, e);
        }
	}
}
