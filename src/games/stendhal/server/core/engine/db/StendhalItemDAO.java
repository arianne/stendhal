/***************************************************************************
 *                 (C) Copyright 2007-2020 - Faiumoni e. V.                *
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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.util.StringUtils;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;

/**
 * data access object for items
 *
 * @author hendrik
 */
public class StendhalItemDAO {
	/** attribute name of itemid */
	public static final String ATTR_ITEM_LOGID = "logid";

	/**
	 * Assigns the next logid to the specified item in case it does not already have one.
	 *
	 * @param transaction database transaction
	 * @param item item
	 * @param timestamp timestamp
	 * @throws SQLException in case of a database error
	 */
	public void itemLogAssignIDIfNotPresent(final DBTransaction transaction, final RPObject item, Timestamp timestamp) throws SQLException {
		if (item.has(ATTR_ITEM_LOGID)) {
			return;
		}

		// insert row into
		String sql = "INSERT INTO item (name, timedate) VALUES ('[name]', '[timedate]')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", item.get("name"));
		params.put("timedate", timestamp);
		transaction.execute(sql, params);

		// get the insert id and store it into the item
		item.put(ATTR_ITEM_LOGID, transaction.getLastInsertId("item", "id"));
		itemLogInsertName(transaction, item, timestamp);
	}


	/**
	 * Logs the name of the item on first.
	 *
	 * @param transaction
	 * @param item
	 * @param timestamp timestamp
	 * @throws SQLException
	 */
	private void itemLogInsertName(final DBTransaction transaction, final RPObject item, Timestamp timestamp) throws SQLException {
		itemLogWriteEntry(transaction, timestamp, item, null, "register", getAttribute(item, "name"), getAttribute(item, "quantity"), getAttribute(item, "infostring"), getAttribute(item, "bound"));
	}
	/**
	 * writes a log entry
	 *
	 * @param transaction DBTransaction
	 * @param timestamp timestamp
	 * @param item item
	 * @param player player object
	 * @param event  name of event
	 * @param param1 param 1
	 * @param param2 param 2
	 * @param param3 param 3
	 * @param param4 param 4
	 * @throws SQLException in case of an database error
	 */
	public  void itemLogWriteEntry(final DBTransaction transaction, Timestamp timestamp, final RPObject item, final RPEntity player, final String event, final String param1, final String param2, final String param3, final String param4) throws SQLException {
		int itemid = item.getInt(StendhalItemDAO.ATTR_ITEM_LOGID);
		itemLogWriteEntry(transaction, timestamp, itemid, player, event, param1, param2, param3, param4);
	}

	/**
	 * writes a log entry
	 *
	 * @param transaction DBTransaction
	 * @param timestamp timestamp
	 * @param itemid itemid of item
	 * @param player player object
	 * @param event  name of event
	 * @param param1 param 1
	 * @param param2 param 2
	 * @param param3 param 3
	 * @param param4 param 4
	 * @throws SQLException in case of an database error
	 */
	public void itemLogWriteEntry(final DBTransaction transaction, Timestamp timestamp, final int itemid, final RPEntity player, final String event, final String param1, final String param2, final String param3, final String param4) throws SQLException {
		String playerName = null;
		if (player != null) {
			playerName = player.getName();
		}
		final String query = "INSERT INTO itemlog (itemid, source, event, "
			+ "param1, param2, param3, param4, timedate) VALUES ("
			+ "[itemid], '[source]', '[event]', '[param1]', '[param2]', '[param3]', '[param4]', '[timedate]');";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemid", itemid);
		params.put("source", StringUtils.trimTo(playerName, 64));
		params.put("event", StringUtils.trimTo(event, 64));
		params.put("param1", StringUtils.trimTo(param1, 64));
		params.put("param2", StringUtils.trimTo(param2, 64));
		params.put("param3", StringUtils.trimTo(param3, 64));
		params.put("param4", StringUtils.trimTo(param4, 64));
		params.put("timedate", timestamp);

		transaction.execute(query, params);
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

}
