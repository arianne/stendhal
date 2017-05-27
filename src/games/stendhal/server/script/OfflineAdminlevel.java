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

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * Changes the admin level of an offline player.
 *
 * @author hendrik
 */
public class OfflineAdminlevel extends AbstractOfflineAction {

	/**
	 * validates the parameters, sends an error message, if something is wrong with them
	 *
	 * @param admin admin executing the script
	 * @param args arguments for the script
	 * @return true if the parameters are valid, false otherwise
	 */
	@Override
	public boolean validateParameters(final Player admin, final List<String> args) {
		if (args.size() != 2) {
			admin.sendPrivateText("/script OfflineAdminlevel.class <playername> <newlevel>");
			return false;
		}
		return true;
	}

	/**
	 * processes the requested operation on the loaded object
	 *
	 * @param admin admin executing the script
	 * @param object the RPObject of the player loaded from the database
	 * @param args arguments for the script
	 */
	@Override
	public void process(final Player admin, RPObject object, final List<String> args) {
		String playerName = args.get(0);
		String newLevel = args.get(1);

		// do the modifications here
		object.put("adminlevel", Integer.parseInt(newLevel));

		// log game event
		new GameEvent(admin.getName(), "adminlevel", playerName, "adminlevel", newLevel).raise();
	}
}
