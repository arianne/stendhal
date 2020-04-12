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

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;


/**
 * Adds some useful string function members to Lua "string" object.
 */
public class LuaStringHelper {

	private static LuaStringHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static LuaStringHelper instance.
	 */
	public static LuaStringHelper get() {
		if (instance == null) {
			instance = new LuaStringHelper();
		}

		return instance;
	}

	/**
	 * Adds custom functions to the Lua "string" object.
	 *
	 * @param stringTable
	 * 		Lua "string" object.
	 */
	public void init(final LuaTable stringTable) {

		stringTable.set("split", new LuaFunction() {
			@Override
			public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
				return split(arg1.strvalue(), arg2.strvalue());
			}
		});

		stringTable.set("isnumber", new LuaFunction() {
			@Override
			public LuaBoolean call(final LuaValue arg) {
				final String st = arg.tojstring();
				try {
					Integer.parseInt(st);
				} catch (final NumberFormatException e) {
					return LuaBoolean.FALSE;
				}

				return LuaBoolean.TRUE;
			}
		});
	}

	/**
	 * Splits a string into a Lua table.
	 *
	 * @param str
	 * 		The string to be split.
	 * @param delim
	 * 		The delimiter character(s) used to split the string.
	 * @return
	 * 		Lua table containing string elements.
	 */
	private LuaTable split(final LuaString str, final LuaString delim) {
		final LuaTable table = new LuaTable();
		final String[] tmpArray = str.tojstring().split(delim.tojstring());

		for (final String st: tmpArray) {
			table.insert(table.length() + 1, LuaValue.valueOf(st));
		}

		return table;
	}
}
