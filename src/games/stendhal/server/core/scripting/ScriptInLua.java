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

import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import games.stendhal.server.entity.player.Player;

/**
 * Manages scripts written in Lua.
 */
public class ScriptInLua extends ScriptingSandbox {

	private final String luaScript;

	public ScriptInLua(String filename) {
		super(filename);

		luaScript = filename;
	}

	@Override
	public boolean load(Player player, List<String> args) {
		Globals globals = JsePlatform.standardGlobals();
		LuaValue chunk = globals.loadfile(luaScript);
		chunk.call();

		return true;
	}
}
