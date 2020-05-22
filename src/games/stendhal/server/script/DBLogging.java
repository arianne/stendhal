/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
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

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandQueueLogger;

/**
 * enabled or disabled db command queue logging
 *
 * @author hendrik
 */
public class DBLogging extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (admin.getAdminLevel() < 2000) {
			admin.sendPrivateText(NotificationType.ERROR, "adminlevel 2000 required,");
			return;
		}

		if (args.size() != 1 || (!args.get(0).equals("true") && !args.get(0).equals("false"))) {
			admin.sendPrivateText("Usage: /script DBLogging.class true|false");
			return;
		}

		if (args.get(0).equals("true")) {
			DBCommandQueueLogger.get().startLogging("/var/log/stendhal/db.log");
		} else {
			DBCommandQueueLogger.get().stopLogging();
		}
	}

}
