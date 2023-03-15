/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.player.Player;


/**
 * Lua script representation.
 */
public class LuaScript extends ScriptingSandbox {

	/** Parent script that called this one if any. */
	final LuaScript parent;
	/** Executable Lua data. */
	final InputStream istream;
	/** Player that called the script. */
	Player caller;
	/** Parameters passed to script call function. */
	List<String> args;


	/**
	 * Creates a Lua script from a data file with a parent script.
	 *
	 * @param parent
	 *     Parent Lua script instance.
	 * @param filename
	 *     Path to external script to be loaded (data/script).
	 */
	LuaScript(final LuaScript parent, final String filename) {
		super(filename);
		this.parent = parent;
		this.istream = null;
	}

	/**
	 * Creates a Lua script from a data file.
	 *
	 * @param filename
	 *     Path to external script (data/script).
	 */
	LuaScript(final String filename) {
		super(filename);
		this.parent = null;
		this.istream = null;
	}

	/**
	 * Creates a Lua script from a resource with a parent script.
	 *
	 * @param parent
	 *     Parent Lua script instance.
	 * @param istream
	 *     Lua data to be loaded.
	 * @param chunkname
	 *     Identifier for this script.
	 */
	LuaScript(final LuaScript parent, final InputStream istream, final String chunkname) {
		super(chunkname);
		this.parent = parent;
		this.istream = istream;
	}

	/**
	 * Creates a Lua script from a resource.
	 *
	 * @param istream
	 *     Lua data to be loaded.
	 * @param chunkname
	 *     Identifier for this script.
	 */
	LuaScript(final InputStream istream, final String chunkname) {
		this(null, istream, chunkname);
	}

	/**
	 * Checks if the script is loaded as a resource.
	 */
	public boolean isResource() {
		return istream != null;
	}

	/**
	 * Checks if this script was called by another script instance.
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Retrieves the parent caller script.
	 */
	public LuaScript getParent() {
		return parent;
	}

	/**
	 * Retrieves player that called script.
	 */
	public Player getCaller() {
		return caller;
	}

	/**
	 * Retrieves list of parameters passed to script.
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * Retrieves the chunk identifier or filename.
	 */
	public String getChunkName() {
		return filename;
	}

	public Path getDirName() {
		if (isResource()) {
			return null;
		}
		final Path dirname = Paths.get(filename).getParent();
		return dirname != null ? dirname : Paths.get("");
	}

	@Override
	public boolean load(final Player caller, final List<String> args) {
		this.caller = caller;
		this.args = args;
		return load();
	}

	/**
	 * Loads & executes the script.
	 *
	 * FIXME: should have a separate function for executing
	 */
	public boolean load() {
		onLoad();

		LuaValue result = LuaValue.NIL;
		if (istream != null) {
			result = loadStream();
		} else {
			result = loadFile();
		}

		boolean success = true;
		if (result.isint() || result.isnil()) {
			success = result.toint() == 0;
		} else if (result.isboolean()) {
			success = result.toboolean();
		}
		if (!hasParent() && !success) {
			// only show return value warnings for first parent script
			LuaLogger.get().warn("Script returned \"" + String.valueOf(result) + "\"");
		}
		onUnload();
		return success;
	}

	/**
	 * Load Lua script from file.
	 *
	 * @return
	 *     LuaValue result returned by the executed script.
	 */
	LuaValue loadFile() {
		// run script
		return LuaLoader.get().getGlobals().loadfile(filename).call();
	}

	/**
	 * Load Lua data from resource stream.
	 *
	 * @return
	 *     LuaValue result returned by the executed script.
	 */
	LuaValue loadStream() {
		LuaValue result = LuaValue.NIL;
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
			// run data chunk
			result = LuaLoader.get().getGlobals().load(reader, filename).call();
			reader.close();
		} catch (final IOException e) {
			Logger.getLogger(LuaScript.class).error(e, e);
			result = LuaValue.ONE;
		}
		return result;
	}

	/**
	 * Action(s) when script is being loaded.
	 */
	private void onLoad() {
		// notify loader
		LuaLoader.get().onLoadScript(this);
	}

	/**
	 * Action(s) when script has completed executing & should be unloaded.
	 */
	private void onUnload() {
		// notify loader
		LuaLoader.get().onUnloadScript(this);
	}
}
