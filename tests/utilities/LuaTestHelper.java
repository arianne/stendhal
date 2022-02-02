/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptInLua;


public class LuaTestHelper {

	private static StendhalQuestSystem qs;
	private static ScriptInLua luaEngine;

	private static boolean initialized = false;


	public static void load(final String script) {
		if (!initialized) {
			qs = StendhalQuestSystem.get();
			luaEngine = new ScriptInLua();
			luaEngine.init();
			initialized = true;
		}

		luaEngine.load(script, null, null);
		qs.loadCachedQuests();
	}
}
