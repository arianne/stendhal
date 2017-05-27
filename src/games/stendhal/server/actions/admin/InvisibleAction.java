/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.INVISIBLE;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class InvisibleAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(INVISIBLE, new InvisibleAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		String state;
		if (player.isInvisibleToCreatures()) {
			player.setInvisible(false);

			state = "off";
		} else {
			player.setInvisible(true);
			state =  "on";
		}
		new GameEvent(player.getName(), INVISIBLE, state).raise();

	}

}
