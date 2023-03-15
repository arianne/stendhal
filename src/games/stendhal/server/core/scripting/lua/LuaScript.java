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
import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.player.Player;


/**
 * Lua script representation.
 */
public class LuaScript extends ScriptingSandbox {

	final InputStream istream;


	public LuaScript(final String filename) {
		super(filename);
		this.istream = null;
	}

	public LuaScript(final InputStream istream, final String chunkname) {
		super(chunkname);
		this.istream = istream;
	}

	/**
	 * Checks if the script is loaded as a resource.
	 */
	public boolean isResource() {
		return istream != null;
	}

	/**
	 * Retrieves the chunk identifier or filename.
	 */
	public String getChunkName() {
		return filename;
	}

	@Override
	public boolean load(final Player player, final List<String> args) {
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
		if (!success) {
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
	private LuaValue loadFile() {
		// run script
		return ScriptInLua.get().getGlobals().loadfile(filename).call();
	}

	/**
	 * Load Lua data from resource stream.
	 *
	 * @return
	 *     LuaValue result returned by the executed script.
	 */
	private LuaValue loadStream() {
		LuaValue result = LuaValue.NIL;
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
			// run data chunk
			result = ScriptInLua.get().getGlobals().load(reader, filename).call();
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
		// set chunk or file name used with logger
		LuaLogger.get().setFilename(filename);
		// notify environment
		ScriptInLua.get().onLoadScript(this);
	}

	/**
	 * Action(s) when script has completed executing & should be unloaded.
	 */
	private void onUnload() {
		// notify environment
		ScriptInLua.get().onUnloadScript(this);
		// clear chunk or file name used with logger
		LuaLogger.get().setFilename(null);
	}
}
