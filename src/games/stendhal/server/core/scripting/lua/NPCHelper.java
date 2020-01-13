/***************************************************************************
 *                   Copyright (C) 2019 - Arianne                          *
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

import org.apache.log4j.Logger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SilentNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;


/**
 * A helper class for adding NPCs to game via Lua scripting engine.
 */
public class NPCHelper {

	// logger instance
	private static final Logger logger = Logger.getLogger(NPCHelper.class);

	/**
	 * Creates a new SpeakerNPC instance.
	 *
	 * @param name
	 * 			String name of new NPC.
	 * @return
	 * 		New SpeakerNPC instance.
	 */
	public SpeakerNPC createSpeakerNPC(final String name) {
		return new SpeakerNPC(name);
	}

	/**
	 * Created a new SilentNPC instance.
	 *
	 * @return
	 * 		New SilentNPC instance.
	 */
	public SilentNPC createSilentNPC() {
		return new SilentNPC();
	}

	/**
	 * Helper function for setting an NPCs path.
	 *
	 * @param entity
	 * 		The NPC instance of which path is being set.
	 * @param table
	 * 		Lua table with list of coordinates representing nodes.
	 */
	public void setPath(final RPEntity entity, final LuaTable table) {

		if (!table.istable()) {
			logger.error("Entity path must be a table");
			return;
		}

		List<Node> nodes = new LinkedList<Node>();

		// Lua table indexing begins at 1
		int index;
		for (index = 1; index <= table.length(); index++) {
			LuaValue point = table.get(index);
			if (point.istable()) {
				LuaValue luaX = ((LuaTable) point).get(1);
				LuaValue luaY = ((LuaTable) point).get(2);

				if (luaX.isinttype() && luaY.isinttype()) {
					Integer X = luaX.toint();
					Integer Y = luaY.toint();

					nodes.add(new Node(X, Y));
				} else {
					logger.error("Path nodes must be integers");
					return;
				}
			} else {
				logger.error("Invalid table data in entity path");
				return;
			}
		}

		if (!nodes.isEmpty()) {
			entity.setPath(new FixedPath(nodes, true));
		} else {
			if (entity.has("name")) {
				logger.warn("Cannot set empty path for entity " + entity.getName());
			} else {
				logger.warn("Cannot set empty path for entity");
			}
		}
	}
}
