/***************************************************************************
 *                      Copyright 2019 (C) - Arianne                       *
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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import games.stendhal.server.core.scripting.lua.NPCHelper;
import games.stendhal.server.entity.mapstuff.sign.Reader;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;

/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua extends ScriptingSandbox {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	private static ScriptInLua instance;
	private static Globals globals;
	private static LuaValue game;

	private final String luaScript;

	// classes to be bound to Lua objects
	private static final Map<String, String> bind_classes = new HashMap<String, String>() {{
		put("ConversationStates", "games.stendhal.server.entity.npc.ConversationStates");
		put("ConversationPhrases", "games.stendhal.server.entity.npc.ConversationPhrases");
		put("CollisionAction", "games.stendhal.server.entity.CollisionAction");
		put("SkinColor", "games.stendhal.common.constants.SkinColor");
	}};


	public ScriptInLua() {
		super(null);

		luaScript = null;
	}

	public ScriptInLua(final String filename) {
		super(filename);

		luaScript = filename;
	}

	public static ScriptInLua getInstance() {
		if (instance == null) {
			instance = new ScriptInLua();
		}

		return instance;
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

	/**
	 * Loads lua master script.
	 */
	public static void init() {
		globals = JsePlatform.standardGlobals();

		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new LuajavaLib());

		// load master script
		final String master = new File(ScriptRunner.class.getPackage().getName().replace(".", "/") + "/lua/init.lua").getPath();
		final URL url = ScriptInLua.class.getClassLoader().getResource(master);

		if (url != null) {
			globals.loadfile(master).call();
		}

		game = CoerceJavaToLua.coerce(getInstance());
		globals.set("game", game);
		globals.set("logger", CoerceJavaToLua.coerce(logger));
		globals.set("npcHelper", CoerceJavaToLua.coerce(new NPCHelper()));

		final StringBuilder sb = new StringBuilder();

		// FIXME: make these methods of npcHelper
		sb.append("newCondition = function(classname, ...)"
				+ " return luajava.newInstance(\"games.stendhal.server.entity.npc.condition.\" .. classname, ...) end\n");
		sb.append("newAction = function(classname, ...)"
				+ " return luajava.newInstance(\"games.stendhal.server.entity.npc.action.\" .. classname, ...) end\n");

		for (final String key: bind_classes.keySet()) {
			sb.append(key + " = luajava.bindClass(\"" + bind_classes.get(key) + "\")\n");
		}

		globals.load(sb.toString()).call();
	}

	/**
	 * Creates a new Sign entity.
	 *
	 * @return
	 * 		Sign object.
	 */
	public Sign createSign() {
		return createSign(true);
	}

	public Sign createSign(final boolean visible) {
		if (visible) {
			return new Sign();
		}

		return new Reader();
	}
}
