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

import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.server.core.pathfinder.Node;


/**
 * Adds some useful function members to Lua "table" table.
 */
public class LuaTableHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaTableHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 *   Static LuaStringHelper instance.
	 */
	public static LuaTableHelper get() {
		if (instance == null) {
			instance = new LuaTableHelper();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaTableHelper() {
		// singleton
	}

	public void init(final LuaTable tableTable) {

		// add table.contains method
		tableTable.set("contains", new LuaFunction() {

			/**
			 * table.contains
			 *
			 * Checks if a table contains a value.
			 *
			 * @param table
			 *   Table to be checked.
			 * @param o
			 *   Object instance to be checked for.
			 * @return
			 *   `LuaBoolean.TRUE` if the object is in the list.
			 */
			@Override
			public LuaBoolean call(final LuaValue table, final LuaValue o) {
				final List<Object> l = LuaTableHelper.toList((LuaTable) table);
				if (l.contains(o.touserdata())) {
					return LuaBoolean.TRUE;
				}
				return LuaBoolean.FALSE;
			}
		});

		// add table.join method
		tableTable.set("join", new LuaFunction() {

			/**
			 * table.join
			 *
			 * Converts a list of strings into a string.
			 *
			 * @param table
			 *   Table to be joined.
			 * @param delim
			 *   Character(s) to be used as separator.
			 * @return
			 *   New `LuaString`.
			 */
			@Override
			public LuaString call(final LuaValue table, final LuaValue delim) {
				final List<String> parts = new LinkedList<>();
				for (final LuaValue key: table.checktable().keys()) {
					parts.add(table.get(key).checkstring().tojstring());
				}

				return (LuaString) CoerceJavaToLua.coerce(String.join(delim.checkjstring(), parts));
			}
		});

		// add table.toList method
		tableTable.set("toList", new LuaFunction() {
			@Override
			public LuaUserdata call(final LuaValue table) {
				return new LuaUserdata(LuaTableHelper.toList(table.checktable()));
			}
		});
	}

	/**
	 * Converts a Lua table pair ({num, num}) to `Node`.
	 *
	 * @param lt
	 *   Table containing integers.
	 */
	public static Node pairToNode(final LuaTable lt) {
		lt.checktable();
		return new Node(lt.get(1).checkint(), lt.get(2).checkint());
	}

	/**
	 * Converts a list of Lua table pairs ({{int, int}, {int, int}}) to list of nodes (`List<Node>`).
	 *
	 * @param lt
	 *   Table containing list of integer pairs.
	 */
	public static List<Node> pairsToNodes(final LuaTable lt) {
		lt.checktable();
		final List<Node> nodes = new LinkedList<>();
		for (int idx=1; idx <= lt.length(); idx++) {
			nodes.add(pairToNode((LuaTable) lt.get(idx)));
		}
		return nodes;
	}

	/**
	 * Converts a Lua table to Java list.
	 *
	 * @param table
	 *   Table with contents to be transferred to new list.
	 * @return
	 *   New `List<Object>` instance.
	 */
	public static List<Object> toList(final LuaTable table) {
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
				objectList.add(LuaTableHelper.toList(lv.checktable()));
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
}
