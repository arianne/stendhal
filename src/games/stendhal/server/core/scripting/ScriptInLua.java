/***************************************************************************
 *                    Copyright © 2019-2023 - Stendhal                     *
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

import java.io.InputStream;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaFunction;
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
import games.stendhal.server.core.scripting.lua.LuaActionHelper;
import games.stendhal.server.core.scripting.lua.LuaArrayHelper;
import games.stendhal.server.core.scripting.lua.LuaConditionHelper;
import games.stendhal.server.core.scripting.lua.LuaEntityHelper;
import games.stendhal.server.core.scripting.lua.LuaLogger;
import games.stendhal.server.core.scripting.lua.LuaMerchantHelper;
import games.stendhal.server.core.scripting.lua.LuaPropertiesHelper;
import games.stendhal.server.core.scripting.lua.LuaQuestHelper;
import games.stendhal.server.core.scripting.lua.LuaScript;
import games.stendhal.server.core.scripting.lua.LuaStringHelper;
import games.stendhal.server.core.scripting.lua.LuaTableHelper;


/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua {

	private static final Logger logger = Logger.getLogger(ScriptInLua.class);

	/** Global objects accessible within Lua scripts. */
	private static Globals globals;
	/** Original `dofile` Lua function. */
	private static LuaFunction dofileOrig;
	/** Script that is currently loaded. */
	private LuaScript currentScript;

	/** Singleton instance. */
	private static ScriptInLua instance;


	/**
	 * Retrieves the singleton instance.
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

	/**
	 * Hidden singleton constructor.
	 */
	private ScriptInLua() {
		init();
	}

	/**
	 * Sets up Lua environment & loads master script.
	 */
	private void init() {
		if (globals != null) {
			logger.warn("Tried to re-initialize Lua environment");
			return;
		}
		logger.info("Initializing Lua environment (" + Lua._VERSION + ")");

		globals = JsePlatform.standardGlobals();

		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new LuajavaLib());

		// store original "dofile" function
		dofileOrig = globals.get("dofile").checkfunction();

		globals.set("logger", CoerceJavaToLua.coerce(LuaLogger.get()));
		globals.set("entities", CoerceJavaToLua.coerce(LuaEntityHelper.get()));
		globals.set("properties", CoerceJavaToLua.coerce(LuaPropertiesHelper.get()));
		globals.set("quests", CoerceJavaToLua.coerce(LuaQuestHelper.get()));
		globals.set("actions", CoerceJavaToLua.coerce(LuaActionHelper.get()));
		globals.set("conditions", CoerceJavaToLua.coerce(LuaConditionHelper.get()));
		globals.set("merchants", CoerceJavaToLua.coerce(LuaMerchantHelper.get()));
		globals.set("arrays", CoerceJavaToLua.coerce(LuaArrayHelper.get()));
		globals.set("grammar", CoerceJavaToLua.coerce(Grammar.get()));
		globals.set("singletons", CoerceJavaToLua.coerce(SingletonRepository.get()));
		globals.set("clones", CoerceJavaToLua.coerce(SingletonRepository.getCloneManager()));
		globals.set("random", CoerceJavaToLua.coerce(new Rand()));

		// initialize supplemental string & table functions
		LuaStringHelper.get().init((LuaTable) globals.get("string"));
		LuaTableHelper.get().init((LuaTable) globals.get("table"));

		// load built-in master script
		final InputStream is = getClass().getResourceAsStream("lua/init.lua");
		if (is != null) {
			if (new LuaScript(is, "init.lua").load()) {
				logger.info("Lua master script loaded: " + getClass().getPackage().getName() + ".lua/init.lua");
			} else {
				logger.warn("Loading Lua master script failed: " + getClass().getPackage().getName() + ".lua/init.lua");
			}
		} else {
			logger.warn("Could not retrieve Lua master script as resource: " + getClass().getPackage().getName() + ".lua/init.lua");
		}
	}

	/**
	 * Create new script instance.
	 *
	 * @param filename
	 *     Path to Lua script.
	 * @return
	 *     Loadable script representation.
	 */
	public LuaScript createScript(final String filename) {
		return new LuaScript(filename);
	}

	/**
	 * Retrieves Lua global objects.
	 */
	public Globals getGlobals() {
		return globals;
	}

	/**
	 * Action when a new script is being loaded.
	 */
	public void onLoadScript(final LuaScript script) {
		currentScript = script;
		// set global game object
		globals.set("game", CoerceJavaToLua.coerce(script));

		final String chunkname = script.getChunkName();
		if (!script.isResource()) {
			// override dofile function to use paths relative to the executing script
			// FIXME: this will fail if called inside scripts called by "dofile"
			globals.set("dofile", new LuaFunction() {
				@Override
				public LuaValue call(final LuaValue lv) {
					if (chunkname == null) {
						return dofileOrig.call(lv);
					}
					return dofileOrig.call(Paths.get(Paths.get(chunkname).getParent().toString(), lv.checkjstring()).toString());
				}
			});
		}
	}

	/**
	 * Action when a script has finished executing.
	 */
	public void onUnloadScript(final LuaScript script) {
		currentScript = null;
		// clear global game object
		globals.set("game", LuaValue.NIL);
	}
}
