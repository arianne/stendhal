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
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;


public class ArraysHelper {

	// static instance
	private static ArraysHelper instance;

	public static ArraysHelper get() {
		if (instance == null) {
			instance = new ArraysHelper();
		}

		return instance;
	}

	/**
	 * Creates a Lua table from a Java array.
	 *
	 * @param list
	 * 		Array containing values.
	 * @return
	 * 		New LuaTable.
	 */
	public LuaTable arrayToTable(final Object[] list) {
		return arrayToTable(Arrays.asList(list));
	}


	/**
	 * Creates a Lua table from a Java list.
	 *
	 * @param list
	 * 		List containing values.
	 * @return
	 * 		New LuaTable.
	 */
	public LuaTable arrayToTable(final List<Object> list) {
		final LuaTable table = new LuaTable();

		for (final Object obj: list) {
			table.insert(table.length() - 1, CoerceJavaToLua.coerce(obj));
		}

		return table;
	}
}
