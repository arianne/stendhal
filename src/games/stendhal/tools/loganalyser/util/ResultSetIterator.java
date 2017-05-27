/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.loganalyser.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Iterates over a database query ResultSet-object doing all the
 * magic that is required to query a ResultSet.
 *
 * @author hendrik
 * @param <T> object type
 */
public abstract class ResultSetIterator<T> implements Iterator<T>, Iterable<T> {
	private static Logger logger = Logger.getLogger(ResultSetIterator.class);

	private final Statement statement;
	protected ResultSet resultSet;
	private boolean hasNext;
	private boolean nextCalled;
	private boolean closed;

	/**
	 * Creates a new ResultSetIterator.
	 *
	 * @param resultSet resultSet
	 */
	public ResultSetIterator(final ResultSet resultSet) {
		this.statement = null;
		this.resultSet = resultSet;
	}

	/**
	 * Creates a new ResultSetIterator.
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public ResultSetIterator(final Statement statement, final ResultSet resultSet) {
		this.statement = statement;
		this.resultSet = resultSet;
	}

	/**
	 * Creates the object instance.
	 *
	 * @return T
	 */
	protected abstract T createObject();

	@Override
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
        } catch (final SQLException e) {
        	hasNext = false;
        	logger.error(e, e);
        }
        if (!hasNext) {
        	close();
        }
    }

	@Override
	public T next() {
		if (!nextCalled) {
			resultSetNext();
		}
		nextCalled = false;
		return createObject();
	}

	@Override
	public void remove() {
		try {
			if (nextCalled) {
				resultSet.previous();
			}
	        resultSet.deleteRow();
			if (nextCalled) {
				resultSet.next();
			}
        } catch (final SQLException e) {
        	logger.error(e, e);
        }
	}

	/**
	 * Closes the resultSet and statement.
	 */
	private void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			resultSet.close();
        } catch (final SQLException e) {
        	logger.error(e, e);
        }
		try {
			if (statement != null) {
				statement.close();
			}
        } catch (final SQLException e) {
        	logger.error(e, e);
        }
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

}
