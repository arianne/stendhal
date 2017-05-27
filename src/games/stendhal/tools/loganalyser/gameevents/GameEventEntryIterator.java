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
package games.stendhal.tools.loganalyser.gameevents;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import games.stendhal.tools.loganalyser.util.ResultSetIterator;

/**
 * Iterates over the log entries returned by a database query.
 *
 * @author hendrik
 */
public class GameEventEntryIterator extends ResultSetIterator<GameEventEntry> {
	private static Logger logger = Logger.getLogger(GameEventEntryIterator.class);

	/**
	 * Creates a new LogEntryIterator.
	 *
	 * @param resultSet resultSet
	 */
	public GameEventEntryIterator(final ResultSet resultSet) {
		super(resultSet);
	}

	/**
	 * Creates a new LogEntryIterator.
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public GameEventEntryIterator(final Statement statement, final ResultSet resultSet) {
		super(statement, resultSet);
	}

	@Override
	protected GameEventEntry createObject() {
		try {
			return new GameEventEntry(resultSet.getString("id"), resultSet.getString("timedate"),
					resultSet.getString("source"), resultSet.getString("event"),
					resultSet.getString("param1"), resultSet.getString("param2"));
		} catch (final SQLException e) {
			logger.error(e, e);
			return null;
		}
	}

}
