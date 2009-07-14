/***************************************************************************
 *                    (C) Copyright 2003-2009 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.RPObjectDAO;

import org.apache.log4j.Logger;

/**
 * iterates over all characters
 *
 * @author hendrik
 */
public class CharacterIterator implements Iterator<RPObject>, Iterable<RPObject> {
	private static Logger logger = Logger.getLogger(CharacterIterator.class);
	private final ResultSet result;

	private final DBTransaction transaction;

	public CharacterIterator(DBTransaction transaction) throws SQLException {
		this.transaction = transaction;
		final String query = "select object_id from characters";

		logger.debug("iterator is executing query " + query);
		result = transaction.query(query, null);
	}

	public boolean hasNext() {
		try {
			return result.next();
		} catch (final SQLException e) {
			logger.error(e, e);
			return false;
		}
	}

	public RPObject next() {
		try {
			final int objectid = result.getInt("object_id");
			return DAORegister.get().get(RPObjectDAO.class).loadRPObject(transaction, objectid);
		} catch (final Exception e) {
			logger.warn(e, e);
			return null;
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<RPObject> iterator() {
		return this;
	}

}
