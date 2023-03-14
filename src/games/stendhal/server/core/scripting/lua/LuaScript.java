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

	@Override
	public boolean load(final Player player, final List<String> args) {
		return load();
	}

	public boolean load() {
		final LuaLogger luaLogger = LuaLogger.get();
		// update logger with current script
		luaLogger.setFilename(filename);

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
			luaLogger.warn("Script returned \"" + String.valueOf(result) + "\"");
		}
		// reset logger script filename
		luaLogger.setFilename(null);
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
}
