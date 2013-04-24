/***************************************************************************
 *                    (C) Copyright 2007-2010 - Stendhal                   *
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

import games.stendhal.server.entity.RPEntity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.StringChecker;
import marauroa.server.db.command.AbstractDBCommand;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * base class for item logging
 *
 * @author hendrik
 */
public abstract class AbstractLogItemEventCommand extends AbstractDBCommand {
	public static final String ATTR_ITEM_LOGID = "logid";

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		log(transaction);
	}



	protected abstract void log(DBTransaction transaction) throws SQLException;

	protected String getQuantity(final RPObject item) {
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		return Integer.toString(quantity);
	}

	/**
	 * Assigns the next logid to the specified item in case it does not already have one.
	 *
	 * @param transaction database transaction
	 * @param item item
	 * @throws SQLException in case of a database error
	 */
	protected void itemLogAssignIDIfNotPresent(final DBTransaction transaction, final RPObject item) throws SQLException {
		if (item.has(ATTR_ITEM_LOGID)) {
			return;
		}

		// insert row into 
		String sql = "INSERT INTO item (name) VALUES ('[name]')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", item.get("name"));
		transaction.execute(sql, params);

		// get the insert id and store it into the item
		item.put(ATTR_ITEM_LOGID, transaction.getLastInsertId("item", "id"));
		itemLogInsertName(transaction, item);
	}

	/**
	 * Logs the name of the item on first.
	 * @param transaction
	 * @param item
	 * @throws SQLException
	 */
	private void itemLogInsertName(final DBTransaction transaction, final RPObject item) throws SQLException {
		itemLogWriteEntry(transaction, item, null, "register", getAttribute(item, "name"), getAttribute(item, "quantity"), getAttribute(item, "infostring"), getAttribute(item, "bound"));
	}

	protected void itemLogWriteEntry(final DBTransaction transaction, final RPObject item, final RPEntity player, final String event, final String param1, final String param2, final String param3, final String param4) throws SQLException {
		int itemid = item.getInt(ATTR_ITEM_LOGID);
		itemLogWriteEntry(transaction, itemid, player, event, param1, param2, param3, param4);
	}

	protected void itemLogWriteEntry(final DBTransaction transaction, final int itemid, final RPEntity player, final String event, final String param1, final String param2, final String param3, final String param4) throws SQLException {
		String playerName = null;
		if (player != null) {
			playerName = player.getName();
		}
		final String query = "INSERT INTO itemlog (itemid, source, event, " 
			+ "param1, param2, param3, param4) VALUES (" 
			+ itemid + ", '" 
			+ StringChecker.trimAndEscapeSQLString(playerName, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(event, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param1, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param2, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param3, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param4, 64) + "');";
	
		transaction.execute(query, null);
	}




	/**
	 * gets an optional attribute .
	 *
	 * @param object object to read the optional attribute from
	 * @param attribute 
	 * @return attribute name of attribute
	 */
	private String getAttribute(final RPObject object, final String attribute) {
		if (object.has(attribute)) {
			return object.get(attribute);
		} else {
			return "null";
		}
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
