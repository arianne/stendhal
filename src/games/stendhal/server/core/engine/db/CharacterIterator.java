/***************************************************************************
 *                    (C) Copyright 2003-2013 - Stendhal                   *
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

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.RPObjectDAO;

/**
 * iterates over all characters
 *
 * @author hendrik
 */
public class CharacterIterator implements Iterator<RPObject>, Iterable<RPObject> {
	private static Logger logger = Logger.getLogger(CharacterIterator.class);
	private final ResultSet result;
	private boolean transform;
	private final DBTransaction transaction;

	/**
	 * creates a character iterator
	 *
	 * @param transaction DBTransaction
	 * @param transform apply Transformat rules
	 * @throws SQLException in case of an database error
	 */
	public CharacterIterator(DBTransaction transaction, boolean transform) throws SQLException {
		this.transaction = transaction;
		this.transform = transform;
		final String query = "select object_id from characters order by object_id";

		logger.debug("iterator is executing query " + query);
		result = transaction.query(query, null);
	}

	@Override
	public boolean hasNext() {
		try {
			return result.next();
		} catch (final SQLException e) {
			logger.error(e, e);
			return false;
		}
	}

	@Override
	public RPObject next() {
		try {
			final int objectid = result.getInt("object_id");
			return DAORegister.get().get(RPObjectDAO.class).loadRPObject(transaction, objectid, transform);
		} catch (final Exception e) {
			logger.warn(e, e);
			return null;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<RPObject> iterator() {
		return this;
	}

}
