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
public abstract class ResultSetIterator<T> implements Iterator<T> {
	private static Logger logger = Logger.getLogger(ResultSetIterator.class);
	
	private ResultSet resultSet;
	private boolean hasNext;
	private boolean nextCalled;

	/**
	 * creates the object instance
	 *
	 * @return T
	 */
	protected abstract T createObject();

	public boolean hasNext() {
		if (nextCalled) {
			return hasNext;
		}
		nextCalled = true;
		resultSetNext();
		return hasNext;
	}

	/**
	 * calls resultSet.next without throwing an exception
	 * (errors are just logged and ignored).
	 */
	private void resultSetNext() {
		try {
	        hasNext = resultSet.next();
        } catch (SQLException e) {
        	hasNext = false;
        	logger.error(e, e);
        }
    }

	public T next() {
		resultSetNext();
		nextCalled = false;
		return createObject();
	}

	public void remove() {
		try {
	        resultSet.deleteRow();
        } catch (SQLException e) {
        	logger.error(e, e);
        }
	}
}
