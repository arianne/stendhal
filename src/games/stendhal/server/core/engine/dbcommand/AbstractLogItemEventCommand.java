/***************************************************************************
 *                    (C) Copyright 2007-2013 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.dbcommand;

import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;

/**
 * base class for item logging
 *
 * @author hendrik
 */
public abstract class AbstractLogItemEventCommand extends AbstractDBCommand {


	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		log(transaction);
	}


	/**
	 * logs the event to the database.
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	protected abstract void log(DBTransaction transaction) throws SQLException;

	/**
	 * gets the quantity from an item; correctly handles non stackable items
	 *
	 * @param item Item
	 * @return quantity
	 */
	protected String getQuantity(final RPObject item) {
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		return Integer.toString(quantity);
	}

}
