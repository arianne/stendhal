/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.entity.player.Player;


/**
 * Admin script to load external Lua scripts manually.
 */
public class ExecuteLua extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.isEmpty()) {
			admin.sendPrivateText(NotificationType.ERROR, "Please specify the script to load: /script ExecuteLua.class script_path");
			return;
		}

		final File script = Paths.get("data/script", String.join(" ", args)).toFile();
		if (!script.isFile()) {
			admin.sendPrivateText(NotificationType.ERROR, "Failed to load Lua script. File not found: " + script.toString().replace("\\", "\\\\"));
			return;
		}

		if (!ScriptInLua.get().load(script.toString(), admin, null)) {
			admin.sendPrivateText(NotificationType.ERROR, "An error occured when trying to load Lua script: " + script.toString().replace("\\", "\\\\"));
		}
	}
}
