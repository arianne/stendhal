/***************************************************************************
 *                   (C) Copyright 2003-2018 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.move;

import static games.stendhal.common.constants.Actions.COND_STOP;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Action that stops player movement when certain conditions are met.
 *
 * @author AntumDeluge
 */
public class ConditionalStopAction implements ActionListener {

	/**
	 * Registers conditional stop action.
	 */
	public static void register() {
		CommandCenter.register(COND_STOP, new ConditionalStopAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		// Auto-walk is checked by player.stop().
		if (!player.stopped() && !player.hasPath()) {
			player.stop();
		}
	}
}
