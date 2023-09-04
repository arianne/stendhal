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
package games.stendhal.server.core.scripting.lua;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaError;
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


/**
 * Manages scripts written in Lua.
 */
public class LuaLoader {

	private static final Logger logger = Logger.getLogger(LuaLoader.class);

	/** Global objects accessible within Lua scripts. */
	private static Globals globals;
	/** Original `dofile` Lua function. */
	@SuppressWarnings("unused")
	private static LuaFunction dofileOrig;
	/** Script that is currently loaded. */
	private LuaScript currentScript;

	/** Singleton instance. */
	private static LuaLoader instance;


	/**
	 * Retrieves the singleton instance.
	 */
	public static LuaLoader get() {
		if (instance == null) {
			instance = new LuaLoader();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaLoader() {
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

		// store original 'dofile' function
		dofileOrig = globals.get("dofile").checkfunction();
		// override 'dofile' function to allow relative paths & use within subscripts
		// TODO: allow use for loading from resource
		final LuaFunction dofile = new LuaFunction() {
			@Override
			public LuaValue call(final LuaValue filename) {
				String filepath = filename.checkjstring();
				// prepend parent caller script's directory for absolute path
				if (currentScript != null && !currentScript.isResource()) {
					final String parentDirname = currentScript.getDirName().toString();
					filepath = Paths.get(parentDirname, filepath).toString();
				}
				// for our purposes Java can handle Unix path node delimiters on Windows
				filepath = filepath.replace("\\", "/");

				// strip parent directory nodes from absolute path
				final String pdelim = "/../";
				if (filepath.contains(pdelim)) {
					try {
						String truncated = filepath;
						final int pdlen = pdelim.length();
						int iter = 0;
						for (int idx = truncated.indexOf(pdelim); idx > -1; idx = truncated.indexOf(pdelim)) {
							final String first = Paths.get(truncated.substring(0, idx)).getParent().toString();
							final String second = truncated.substring(idx + pdlen);
							truncated = Paths.get(first, second).toString();
							// safety
							iter++;
							if (iter > 100) {
								LuaLogger.get().error(new LuaError(
										"Cannot process file path in 'dofile', too many parent nodes: " + filepath));
								return LuaValue.ONE;
							}
						}
						filepath = truncated;
					} catch (final NullPointerException e) {
						LuaLogger.get().error(new LuaError("Invalid file path in 'dofile': " + filepath));
						return LuaValue.ONE;
					}
				}

				final LuaScript script = new LuaScript(currentScript, filepath);
				// don't allow scripts to be run outside of data/script directory
				if (!filepath.startsWith("data/script/") && !script.isResource()) {
					LuaLogger.get().error(new LuaError("'dofile' cannot run scripts outside of data/script directory"));
					return LuaValue.ONE;
				}
				return LuaValue.valueOf(script.load());
			}
		};

		globals.set("dofile", CoerceJavaToLua.coerce(dofile));
		globals.set("logger", CoerceJavaToLua.coerce(LuaLogger.get()));
		globals.set("game", CoerceJavaToLua.coerce(LuaWorldHelper.get()));
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
		final InputStream is = getClass().getResourceAsStream("init.lua");
		final String chunkname = getClass().getPackage().getName() + "/init.lua";
		if (is != null) {
			if (new LuaScript(is, chunkname).load()) {
				logger.info("Lua master script loaded: " + chunkname);
			} else {
				logger.warn("Loading Lua master script failed: " + chunkname);
			}
		} else {
			logger.warn("Could not retrieve Lua master script as resource: " + chunkname);
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
		// pass new script ID to logger
		LuaLogger.get().setScript(script);
	}

	/**
	 * Action when a script has finished executing.
	 */
	public void onUnloadScript(final LuaScript script) {
		if (script.hasParent()) {
			currentScript = script.getParent();
		} else {
			currentScript = null;
		}
		// restore parent script ID or unset ID used with logger messages
		LuaLogger.get().setScript(currentScript);
	}

	/**
	 * Retrieves Lua module initialization scripts from "data/mods/" directory.
	 *
	 * Note: These modules are separate from the regular "data/script" scripts & must
	 *       be named "init.lua".
	 *
	 * @return
	 * 		List of loadable Lua scripts.
	 */
	private List<String> getMods() {
		final List<String> modlist = new ArrayList<String>();

		final URL url = getClass().getClassLoader().getResource("data/mods/");
		if (url != null) {
			final String modroot = url.getFile();

			// regular files in root mod directory are ignored
			for (final File dir: new File(modroot).listFiles(File::isDirectory)) {
				try {
					final Stream<Path> paths = Files.walk(Paths.get(dir.toString())).filter(Files::isRegularFile);
					for (String filepath: paths.map(s -> s.toString()).collect(Collectors.toList())) {
						// trim absolute path prefix
						filepath = filepath.substring(modroot.length() - 1);

						// mods must use an initialization script name "init.lua"
						if (new File(filepath).getName().equals("init.lua")) {
							modlist.add(filepath);
						}
					}
				} catch (final IOException e1) {
					logger.error("Error while recursing mods");
					e1.printStackTrace();
					return null;
				}
			}
		}

		return modlist;
	}

	/**
	 * Initializes Lua init scripts in mods directory.
	 */
	@SuppressWarnings("unused")
	private void initMods() {
		for (final String modpath: getMods()) {
			new LuaScript(modpath).load();
		}
	}
}
