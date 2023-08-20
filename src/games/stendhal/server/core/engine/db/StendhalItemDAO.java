/***************************************************************************
 *                 (C) Copyright 2007-2023 - Faiumoni e. V.                *
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
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
	private static final Logger logger = Logger.getLogger(StendhalItemDAO.class);

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
	/**
	 * Dumps the properties of the specified object into the prepared statement as an operation in
	 * a batch.
	 *
	 * @param stmt
	 *   PreparedStatement in batch mode.
	 * @param item
	 *   DefaultItem
	 * @throws
	 *   SQLException in case a database error is thrown.
	 */
	private void dump(PreparedStatement stmt, DefaultItem item) throws SQLException {
		stmt.setInt(1, 1);
		stmt.setString(2, item.getItemName());
		stmt.setString(3, item.getItemClass());
		stmt.setString(4, item.getItemSubclass());
		stmt.setString(5, item.getDescription());
		stmt.setDouble(6, item.getWeight());
		stmt.setInt(7, item.getValue());
		stmt.setString(8, item.getAttributes().get("min_level"));

		stmt.setString(9, item.getAttributes().get("atk"));
		stmt.setString(10, item.getAttributes().get("ratk"));
		stmt.setString(11, item.getAttributes().get("rate"));
		stmt.setString(12, item.getAttributes().get("def"));
		stmt.setString(13, item.getAttributes().get("range"));
		stmt.setString(14, toStringOrNull(item.getDamageType()));
		stmt.setString(15, item.getAttributes().get("lifesteal"));

		stmt.setString(16, item.getAttributes().get("amount"));
		stmt.setString(17, item.getAttributes().get("regen"));
		stmt.setString(18, item.getAttributes().get("frequency"));
		stmt.setString(19, item.getAttributes().get("immunization"));
		stmt.setString(20, item.getAttributes().get("antipoison"));
		stmt.setString(21, item.getAttributes().get("life_support"));

		stmt.setString(22, toStringOrNull(toClassNameOrNull(item.getImplementation())));
		stmt.setString(23, toClassStringOrNull(item.getUseBehavior()));
		stmt.setString(24, item.getAttributes().get("infostring"));
		stmt.setString(25, item.getAttributes().get("menu"));
		stmt.setString(26, item.getAttributes().get("use_sound"));
		stmt.setString(27, item.getAttributes().get("persistent"));
		stmt.setString(28, item.getAttributes().get("slot_name"));
		stmt.setString(29, item.getAttributes().get("slot_size"));
		
		stmt.setString(30, item.getAttributes().get("undroppableondeath"));
		stmt.setInt(31, MathHelper.parseIntDefault(item.getAttributes().get("autobind"), 0));
		stmt.setString(32, item.getAttributes().get("max_quantity"));
		stmt.setString(33, item.getAttributes().get("deterioration"));
		stmt.setInt(34, item.isUnattainable() ? 1: 0);

		/*
		private List<String> slots = null;
		private Map<String, String> attributes = null;
		private Map<Nature, Double> susceptibilities;
		private Map<StatusType, Double> resistances;
		private String[] statusAttacks;
		private List<String> activeSlotsList;
		 */
		stmt.addBatch();
	}

	public String toStringOrNull(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	public String toClassStringOrNull(Object o) {
		if (o == null) {
			return null;
		}
		return o.getClass().getName();
	}

	private String toClassNameOrNull(Class<?> c) {
		if (c == null) {
			return null;
		}
		return c.getName();
	}


	/**
	 * dumps all NPCs
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dump(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		transaction.execute("UPDATE iteminfo SET active=0", null);
		PreparedStatement stmt = transaction.prepareStatement("UPDATE iteminfo SET "
				+ "active=?, name=?, class=?, subclass=?, description=?, weight=?, value=?, min_level=?, "
				+ "atk=?, ratk=?, rate=?, def=?, projectile_range=?, damage_type=?, lifesteal=?, "
				+ "amount=?, regen=?, frequency=?, immunization=?, antipoison=?, life_support=?, "
				+ "implementation=?, use_behavior=?, infostring=?, menu=?, use_sound=?, persistent=?, "
				+ "slot_name=?, slot_size=?, undroppableondeath=?, autobind=?, max_quantity=?, "
				+ "deterioration=?, unattainable=? "
				+ "WHERE name=?", null);
		Map<String, DefaultItem> unknown = new HashMap<>();
		EntityManager entityManager = SingletonRepository.getEntityManager();
		Collection<DefaultItem> defaultItems = entityManager.getDefaultItems();
		for (DefaultItem item : defaultItems) {
			unknown.put(item.getItemName().trim(), item);
			stmt.setString(35, item.getItemName());
			dump(stmt, item);
		}
		stmt.executeBatch();


		// add new
		ResultSet resultSet = transaction.query("SELECT name FROM iteminfo", null);
		while (resultSet.next()) {
			unknown.remove(resultSet.getString(1));
		}

		stmt = transaction.prepareStatement("INSERT INTO iteminfo "
				+ "(active, name, class, subclass, description, weight, value, min_level, "
				+ "atk, ratk, rate, def, projectile_range, damage_type, lifesteal, "
				+ "amount, regen, frequency, immunization, antipoison, life_support, "
				+ "implementation, use_behavior, infostring, menu, use_sound, persistent, "
				+ "slot_name, slot_size, undroppableondeath, autobind, max_quantity, "
				+ "deterioration, unattainable) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		for (DefaultItem item : unknown.values()) {
			dump(stmt, item);
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of items in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}


	public Map<String, Integer> getItemInfoIdMap(DBTransaction transaction) throws SQLException {
		return transaction.queryAsMap("SELECT name, id FROM iteminfo", null);
	}
}

