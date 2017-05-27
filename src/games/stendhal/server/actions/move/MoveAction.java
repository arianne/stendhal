/***************************************************************************
 *                   (C) Copyright 2003-2013 - Marauroa                    *
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

import static games.stendhal.common.constants.Actions.DIR;
import static games.stendhal.common.constants.Actions.MOVE;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * moves into the specified direction
 */
public class MoveAction implements ActionListener {

	/**
	 * registers the action
	 */
	public static void register() {
		final MoveAction move = new MoveAction();
		CommandCenter.register(MOVE, move);
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(DIR)) {
			final int dirval = action.getInt(DIR);

			if (dirval < 0) {
				player.removeClientDirection(Direction.build(-dirval));
			} else {
				player.addClientDirection(Direction.build(dirval));
			}

			player.applyClientDirection(true);
		}

		TutorialNotifier.move(player);
		player.notifyWorldAboutChanges();
	}
}
