/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class StopAction implements ActionListener {

	public static void register() {
		CommandCentre.register("stop", new StopAction());
	}

	public void onAction(Player player, RPAction action) {


		player.stop();

		if (action.has("attack")) {
			player.stopAttack();
		}
		player.notifyWorldAboutChanges();


	}
}
