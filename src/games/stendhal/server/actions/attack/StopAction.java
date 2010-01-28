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
package games.stendhal.server.actions.attack;

import static games.stendhal.common.constants.Actions.ATTACK;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class StopAction implements ActionListener {



	public static void register() {
		CommandCenter.register("stop", new StopAction());
	}

	public void onAction(final Player player, final RPAction action) {

		player.stop();

		if (action.has(ATTACK)) {
			player.stopAttack(player.getAttackTarget());
		}

		player.notifyWorldAboutChanges();
	}
}
