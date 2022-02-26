/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;


/**
 * Handles some conversion of Java arrays to Lua tables.
 */
public class LuaArrayHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaArrayHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static ArraysHelper instance.
	 */
	public static LuaArrayHelper get() {
		if (instance == null) {
			instance = new LuaArrayHelper();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaArrayHelper() {
		// singleton
	}

	/**
	 * Creates a Lua table from a Java array.
	 *
	 * @param list
	 * 		Array containing values.
	 * @return
	 * 		New LuaTable.
	 */
	public LuaTable toTable(final Object[] list) {
		return toTable(Arrays.asList(list));
	}


	/**
	 * Creates a Lua table from a Java list.
	 *
	 * @param list
	 * 		List containing values.
	 * @return
	 * 		New LuaTable.
	 */
	public LuaTable toTable(final List<Object> list) {
		final LuaTable table = new LuaTable();

		for (final Object obj: list) {
			table.insert(table.length() - 1, CoerceJavaToLua.coerce(obj));
		}

		return table;
	}

	/**
	 * Converts a Lua table to Java list.
	 *
	 * @param table
	 * 		Table with contents to be transferred to new list.
	 * @return
	 * 		New <code>List<Object></code> instance.
	 */
	public List<Object> toList(final LuaTable table) {
		final List<Object> objectList = new LinkedList<>();

		for (final LuaValue key: table.keys()) {
			final LuaValue lv = table.get(key);

			if (lv.isnil()) {
				objectList.add(null);
			} else if (lv.isnumber()) {
				if (lv.isint()) {
					objectList.add(lv.toint());
				} else if (lv.islong()) {
					objectList.add(lv.tolong());
				} else {
					objectList.add(lv.todouble());  // all other number types to double
				}
			} else if (lv.isboolean()) {
				objectList.add(lv.toboolean());
			} else if (lv.istable()) {
				objectList.add(toList(lv.checktable()));
			} else if (lv.isuserdata()) {
				objectList.add(lv.touserdata());
			} else if (lv.isstring()) {
				objectList.add(lv.tojstring());
			} else {
				logger.warn("Data type not added: " + lv.typename());
			}
		}

		return objectList;
	}

	/**
	 * Converts a Lua table to Java array.
	 *
	 * @param table
	 * 		Table with contents to be transferred to new array.
	 * @return
	 * 		New <code>Object[]</code> instance.
	 */
	public Object[] toArray(final LuaTable table) {
		return toList(table).toArray();
	}
}
