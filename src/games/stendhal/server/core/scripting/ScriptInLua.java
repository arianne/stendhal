/***************************************************************************
 *                      (C) Copyright 2018 - Arianne                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua extends ScriptingSandbox {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	private Globals globals;

	private final String luaScript;

	private LuaValue game;

	public ScriptInLua(String filename) {
		super(filename);

		globals = JsePlatform.standardGlobals();
		luaScript = filename;

		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new LuajavaLib());

		game = CoerceJavaToLua.coerce(this);
		globals.set("game", game);
		globals.set("logger", CoerceJavaToLua.coerce(logger));
		globals.set("stendhal", CoerceJavaToLua.coerce(new LuaHelper()));
		globals.load(
				"ConversationStates = luajava.bindClass(\"games.stendhal.server.entity.npc.ConversationStates\")\n"
				+ "ConversationPhrases = luajava.bindClass(\"games.stendhal.server.entity.npc.ConversationPhrases\")").call();
	}

	/**
	 * Initial load of the script.
	 *
	 * @param player
	 * 			The admin who loads script or <code>null</code> on server start.
	 * @param args
	 * 			The arguments the admin specified or <code>null</code> on server start.
	 */
	@Override
	public boolean load(Player player, List<String> args) {
		LuaValue chunk = globals.loadfile(luaScript);
		chunk.call();

		return true;
	}
}


/**
 * A helper class for adding objects to game via Lua scripting engine.
 */
class LuaHelper {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	/**
	 * Creates a new SpeakerNPC instance.
	 *
	 * @param name
	 * 			String name of new NPC.
	 * @return
	 */
	public SpeakerNPC createNPC(final String name) {
		return new SpeakerNPC(name);
	}

	public void setEntityPath(final RPEntity entity, final LuaTable table) {

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
