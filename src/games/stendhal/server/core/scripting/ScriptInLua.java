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
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.scripting.lua.LuaActionHelper;
import games.stendhal.server.core.scripting.lua.LuaArrayHelper;
import games.stendhal.server.core.scripting.lua.LuaConditionHelper;
import games.stendhal.server.core.scripting.lua.LuaEntityHelper;
import games.stendhal.server.core.scripting.lua.LuaMerchantHelper;
import games.stendhal.server.core.scripting.lua.LuaPropertiesHelper;
import games.stendhal.server.core.scripting.lua.LuaQuestHelper;
import games.stendhal.server.core.scripting.lua.LuaStringHelper;
import games.stendhal.server.core.scripting.lua.LuaTableHelper;
import games.stendhal.server.entity.mapstuff.sound.BackgroundMusicSource;
import games.stendhal.server.entity.player.Player;

/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua extends ScriptingSandbox {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	/** The singleton instance. */
	private static ScriptInLua instance;

	private static final SingletonRepository singletons = SingletonRepository.get();

	private static Globals globals;

	private static String luaScript;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static ScriptInLua get() {
		if (instance == null) {
			instance = new ScriptInLua();
		}

		return instance;
	}

	/**
	 * @deprecated
	 *     Use @ref ScriptInLua.get().
	 */
	@Deprecated
	public static ScriptInLua getInstance() {
		return get();
	}

	ScriptInLua() {
		super(null);

		// assign singleton instance for safety
		instance = this;
		luaScript = null;
	}

	ScriptInLua(final String filename) {
		super(filename);

		// assign singleton instance for safety
		instance = this;
		luaScript = filename;
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

		// reset member back to null state after loaded
		luaScript = null;

		return result;
	}

	/**
	 * For manually loading an external Lua script.
	 *
	 * @param filename
	 * 		Relative path to script (usually in "data/script").
	 * @param player
	 * 		The admin who loads the script.
	 * @param args
	 * 		The arguments the admin specified.
	 * @return
	 * 		<code>true</code> if loading succeeded.
	 */
	public boolean load(final String filename, final Player player, final List<String> args) {
		luaScript = filename;
		return load(player, args);
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

		globals.set("game", CoerceJavaToLua.coerce(get()));
		globals.set("logger", CoerceJavaToLua.coerce(LuaLogger.get()));
		globals.set("entities", CoerceJavaToLua.coerce(LuaEntityHelper.get()));
		globals.set("properties", CoerceJavaToLua.coerce(LuaPropertiesHelper.get()));
		globals.set("quests", CoerceJavaToLua.coerce(LuaQuestHelper.get()));
		globals.set("actions", CoerceJavaToLua.coerce(LuaActionHelper.get()));
		globals.set("conditions", CoerceJavaToLua.coerce(LuaConditionHelper.get()));
		globals.set("merchants", CoerceJavaToLua.coerce(LuaMerchantHelper.get()));
		globals.set("arrays", CoerceJavaToLua.coerce(LuaArrayHelper.get()));
		globals.set("grammar", CoerceJavaToLua.coerce(Grammar.get()));
		globals.set("singletons", CoerceJavaToLua.coerce(singletons));
		globals.set("clones", CoerceJavaToLua.coerce(singletons.getCloneManager()));
		globals.set("random", CoerceJavaToLua.coerce(new Rand()));

		// initialize supplemental string & table functions
		LuaStringHelper.get().init((LuaTable) globals.get("string"));
		LuaTableHelper.get().init((LuaTable) globals.get("table"));

		// override dofile function to use relative paths
		// FIXME: this only works at script initialization because it depends on "luaScript" variable
		final LuaFunction dofileOrig = globals.get("dofile").checkfunction();
		globals.set("dofile", new LuaFunction() {
			@Override
			public LuaValue call(final LuaValue lv) {
				if (luaScript == null) {
					return dofileOrig.call(lv);
				}

				return dofileOrig.call(Paths.get(Paths.get(luaScript).getParent().toString(), lv.checkjstring()).toString());
			}
		});

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
	 * Sets the background music for the current zone.
	 *
	 * @param filename
	 * 		File basename excluding .ogg extension.
	 * @param args
	 * 		Lua table of key=value integer values. Valid keys are `volume`,
	 * 		`x`, `y`, & `radius`.
	 */
	public void setMusic(final String filename, final LuaTable args) {
		// default values
		int volume = 100;
		int x = 1;
		int y = 1;
		int radius = 10000;

		for (final LuaValue lkey: args.keys()) {
			final String key = lkey.tojstring();
			final LuaInteger lvalue = (LuaInteger) args.get(lkey);

			if (!lvalue.isnil()) {
				if (key.equals("volume")) {
					volume = lvalue.toint();
				} else if (key.equals("x")) {
					x = lvalue.toint();
				} else if (key.equals("y")) {
					y = lvalue.toint();
				} else if (key.equals("radius")) {
					radius = lvalue.toint();
				} else {
					logger.warn("Unknown table key in game:setMusic: " + key);
				}
			}
		}

		final BackgroundMusicSource musicSource = new BackgroundMusicSource(filename, radius, volume);
		musicSource.setPosition(x, y);
		add(musicSource);
	}

	/**
	 * Sets the background music for the current zone.
	 *
	 * @param filename
	 * 		File basename excluding .ogg extension.
	 */
	public void setMusic(final String filename) {
		setMusic(filename, new LuaTable());
	}

	/**
	 * Executes a function after a specified number of turns.
	 *
	 * FIXME: how to invoke with parameters
	 *
	 * @param turns
	 *     Number of turns to wait.
	 * @param func
	 *     The function to be executed.
	 */
	public void runAfter(final int turns, final LuaFunction func) {
		SingletonRepository.getTurnNotifier().notifyInTurns(turns, new TurnListener() {
			public void onTurnReached(final int currentTurn) {
				func.call();
			}
		});
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

		private void formatMessage(String message) {
			message = message.trim();

			if (luaScript == null) {
				message = "(unknown source) " + message;
			} else {
				message = "(" + luaScript + ") " + message;
			}
		}

		public void info(String message) {
			formatMessage(message);
			logger.info(message);
		}

		public void warn(String message) {
			formatMessage(message);
			logger.warn(message);
		}

		public void error(String message) {
			formatMessage(message);
			logger.error(message);
		}

		public void error(final Object obj, final Throwable throwable) {
			logger.error(obj, throwable);
		}

		public void debug(String message) {
			formatMessage(message);
			logger.debug(message);
		}
	}
}
