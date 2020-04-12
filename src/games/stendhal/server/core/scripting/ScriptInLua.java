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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.scripting.lua.ActionHelper;
import games.stendhal.server.core.scripting.lua.ArraysHelper;
import games.stendhal.server.core.scripting.lua.ConditionHelper;
import games.stendhal.server.core.scripting.lua.EntityHelper;
import games.stendhal.server.core.scripting.lua.LuaStringHelper;
import games.stendhal.server.core.scripting.lua.MerchantHelper;
import games.stendhal.server.core.scripting.lua.PropertiesHelper;
import games.stendhal.server.core.scripting.lua.QuestHelper;
import games.stendhal.server.entity.player.Player;

/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua extends ScriptingSandbox {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	private static ScriptInLua instance;
	private static Globals globals;

	private static String luaScript;


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
	public boolean load(final Player player, final List<String> args) {
		if (luaScript == null) {
			logger.error("Attempted to load null Lua script");
			return false;
		}

		final LuaFunction chunk = (LuaFunction) globals.loadfile(luaScript);
		final LuaValue lresult = chunk.call();

		boolean result = true;
		if (lresult.isint()) {
			result = lresult.toint() == 0;
		} else if (lresult.isboolean()) {
			result = lresult.toboolean();
		}

		if (!result) {
			logger.warn("Lua script return non-zero or \"false\": " + luaScript);
		}

		return result;
	}

	/**
	 * Loads lua master script.
	 */
	public void init() {
		logger.info("Initializing Lua scripting engine");

		globals = JsePlatform.standardGlobals();

		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new LuajavaLib());

		globals.set("game", CoerceJavaToLua.coerce(getInstance()));
		globals.set("logger", CoerceJavaToLua.coerce(LuaLogger.get()));
		globals.set("entities", CoerceJavaToLua.coerce(EntityHelper.get()));
		globals.set("properties", CoerceJavaToLua.coerce(PropertiesHelper.get()));
		globals.set("quests", CoerceJavaToLua.coerce(QuestHelper.get()));
		globals.set("actions", CoerceJavaToLua.coerce(ActionHelper.get()));
		globals.set("conditions", CoerceJavaToLua.coerce(ConditionHelper.get()));
		globals.set("merchants", CoerceJavaToLua.coerce(MerchantHelper.get()));
		globals.set("arrays", CoerceJavaToLua.coerce(ArraysHelper.get()));
		globals.set("grammar", CoerceJavaToLua.coerce(Grammar.get()));

		// initialize supplemental string functions
		LuaStringHelper.get().init((LuaTable) globals.get("string"));

		// load built-in master script
		final InputStream is = getClass().getResourceAsStream("lua/init.lua");
		if (is != null) {
			try {
				final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				final LuaValue result = globals.load(reader, "init.lua").call();
				reader.close();

				if (!result.toboolean()) {
					logger.warn("Loading Lua master script failed: " + getClass().getPackage().getName() + ".lua/init.lua");
				} else {
					logger.info("Lua master script loaded: " + getClass().getPackage().getName() + ".lua/init.lua");
				}
			} catch (final IOException e) {
				logger.error(e, e);
			}
		} else {
			logger.warn("Could not retrieve Lua master script as resource: " + getClass().getPackage().getName() + ".lua/init.lua");
		}
	}


	/**
	 * Handles logging from Lua.
	 */
	public static class LuaLogger {

		private static LuaLogger instance;


		/**
		 * Retrieves the static instance.
		 *
		 * @return
		 * 		Static LuaLogger instance.
		 */
		public static LuaLogger get() {
			if (instance == null) {
				instance = new LuaLogger();
			}

			return instance;
		}

		public void info(String message) {
			message = message.trim();

			if (luaScript == null) {
				message = "(unknown source) " + message;
			} else {
				message = "(" + luaScript + ") " + message;
			}

			logger.info(message);
		}

		public void warn(String message) {
			message = message.trim();

			if (luaScript == null) {
				message = "(unknown source) " + message;
			} else {
				message = "(" + luaScript + ") " + message;
			}

			logger.warn(message);
		}

		public void error(String message) {
			message = message.trim();

			if (luaScript == null) {
				message = "(unknown source) " + message;
			} else {
				message = "(" + luaScript + ") " + message;
			}

			logger.error(message);
		}
	}
}
