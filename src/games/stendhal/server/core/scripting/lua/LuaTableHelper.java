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

import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.server.core.pathfinder.Node;


/**
 * Adds some useful function members to Lua "table" object.
 */
public class LuaTableHelper {

	/** The singleton instance. */
	private static LuaTableHelper instance;

	private static final LuaArrayHelper arrayHelper = LuaArrayHelper.get();


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static LuaStringHelper instance.
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

		/** add table.contains method */
		tableTable.set("contains", new LuaFunction() {

			/**
			 * Checks if a table contains a value.
			 *
			 * @param table
			 * 		Table to be checked.
			 * @param o
			 * 		Object instance to be checked for.
			 * @return
			 * 		<code>LuaBoolean.TRUE</code> if the object is in the list.
			 */
			@Override
			public LuaBoolean call(final LuaValue table, final LuaValue o) {
				final List<Object> l = arrayHelper.toList((LuaTable) table);
				if (l.contains(o.touserdata())) {
					return LuaBoolean.TRUE;
				}

				return LuaBoolean.FALSE;
			}
		});

		/** add table.join method */
		tableTable.set("join", new LuaFunction() {

			/**
			 * Joins a table of strings into a string.
			 *
			 * @param table
			 * 		Table to be joined.
			 * @param delim
			 * 		Character(s) to be used as separator.
			 * @return
			 * 		New LuaString.
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
	}

	/**
	 * Converts a Lua table pair ({num, num}) to `Node`.
	 */
	public static Node pairToNode(final LuaTable lt) {
		lt.checktable();
		return new Node(lt.get(1).checkint(), lt.get(2).checkint());
	}

	/**
	 * Converts a Lua table of table pairs ({{int, int}, {int, int}}) to
	 * list of nodes (`List<Node>`).
	 */
	public static List<Node> pairsToNodes(final LuaTable lt) {
		lt.checktable();
		final List<Node> nodes = new LinkedList<>();

		for (int idx=1; idx <= lt.length(); idx++) {
			nodes.add(pairToNode((LuaTable) lt.get(idx)));
		}

		return nodes;
	}
}
