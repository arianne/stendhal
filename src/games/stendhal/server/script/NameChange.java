/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Configuration;

/**
 * Makes client display a fake player name by changing the title attribute. If
 * args[0] equals remove, the original name is reset. Can only be used to *chage
 * the name of the player running the script.
 *
 * @author timothyb89
 */
public class NameChange extends ScriptImpl {
	private static final String CONFIG_KEY = "stendhal.scripts.namechange.enabled";

	@Override
	public void execute(final Player admin, final List<String> args) {
		// check configuration
		try {
			if (!Configuration.getConfiguration().has(CONFIG_KEY)
					|| !Boolean.parseBoolean(Configuration.getConfiguration().get(
							CONFIG_KEY))) {
				admin.sendPrivateText("This script must be enabled in the server configuration file (usually server.ini) with key "
						+ CONFIG_KEY);
				return;
			}
		} catch (final Exception e) {
			admin.sendPrivateText(e.toString());
			return;
		}
		if (args.size() < 1) {
			admin.sendPrivateText("Usage: /script NameChange.class {newname|remove}\nSets your display name to newname, or removes the name change effect.\nWarning: Not supported for normal characters. Bound items and spouses will be broken and there may be other unexpected effects.");
		} else {
			// do title change
			if (args.get(0).equals("remove")) {
				admin.setTitle(null);
				admin.sendPrivateText("Your original name has been restored. Please change zones for the changes to take effect.");
			} else {
				final String title = args.get(0);

				admin.setTitle(title);
				admin.sendPrivateText("Your display name has been changed to " + title
					+ ". Internally stored names have not been changed and there may be unexpected effects.");
			}

			admin.notifyWorldAboutChanges();
		}
	}
}
