package games.stendhal.tools.itemlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * iterates over a database query ResultSet-object doing all the
 * magic that is required to query a ResultSet.
 *
 * @author hendrik
 * @param <T> object type
 */
public abstract class ResultSetIterator<T> implements Iterator<T> {
	private static Logger logger = Logger.getLogger(ResultSetIterator.class);
	
	private Statement statement;
	private ResultSet resultSet;
	private boolean hasNext;
	private boolean nextCalled;
	private boolean closed;

	/**
	 * creates a new ResultSetIterator
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public ResultSetIterator(Statement statement, ResultSet resultSet) {
		this.statement = statement;
		this.resultSet = resultSet;
	}

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
        if (!hasNext) {
        	close();
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

	/**
	 * closed the resultSet and statement
	 */
	private void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			resultSet.close();
			statement.close();
        } catch (SQLException e) {
        	logger.error(e, e);
        }
	}
}
