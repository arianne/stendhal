/***************************************************************************
 *                    Copyright © 2020-2023 - Stendhal                     *
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
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;


/**
 * Handles some conversion between Java arrays or lists & Lua tables.
 */
public class LuaArrayHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaArrayHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 *   Static ArraysHelper instance.
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
	 * Converts a Java array or `List` to Lua table.
	 *
	 * @param list
	 *   Array containing values.
	 * @return
	 *   New `LuaTable` with contents of ___list___ added.
	 */
	public LuaTable toTable(final Object[] list) {
		return toTable(Arrays.asList(list));
	}

	/**
	 * Converts a Java array or `List` to Lua table.
	 *
	 * @param list
	 *   List containing values.
	 * @return
	 *   New `LuaTable`with contents of ___list___ added.
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
	 *   Table with contents to be transferred to new list.
	 * @return
	 *   New `List<Object>` instance.
	 * @deprecated
	 *   Use `LuaTableHelper.toList`.
	 */
	public List<Object> toList(final LuaTable table) {
		logger.deprecated(LuaArrayHelper.class.getName() + ".toList",
				LuaTableHelper.class.getName() + ".toList");

		return LuaTableHelper.toList(table);
	}

	/**
	 * Converts an indexed Lua table to Java array.
	 *
	 * @param table
	 *   Table with contents to be transferred to new array.
	 * @return
	 *   New `Object[]` instance.
	 */
	public Object[] fromTable(final LuaTable table) {
		return LuaTableHelper.toList(table).toArray();
	}

	/**
	 * Converts an indexed Lua table to Java array.
	 *
	 * @param table
	 *   Table with contents to be transferred to new array.
	 * @return
	 *   New `Object[]` instance.
	 * @deprecated
	 *   Use `LuaArrayHelper.fromTable`.
	 */
	@Deprecated
	public Object[] toArray(final LuaTable table) {
		logger.deprecated(LuaArrayHelper.class.getName() + ".toArray", LuaArrayHelper.class.getName()
				+ ".fromTable");

		return fromTable(table);
	}
}
