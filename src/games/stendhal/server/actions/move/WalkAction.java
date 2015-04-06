/***************************************************************************
 *                     (C) Copyright 2003 - Arianne                        *
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

import static games.stendhal.common.constants.Actions.WALK;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Action that sets character speed and begins moving in faced direction.
 * 
 * @author AntumDeluge
 */
public class WalkAction implements ActionListener {

	/**
	 * Registers walk action.
	 */
	public static void register() {
		CommandCenter.register(WALK, new WalkAction());
	}

	/**
	 * Begin walking.
	 */
	@Override
	public void onAction(Player player, RPAction action) {
		/* The speed at which the player will walk. */
		final double currentSpeed = player.getSpeed();
		final double newSpeed = player.getBaseSpeed();
		final Direction walkDirection = player.getDirection();

		if (player.stopped()) {
			/* Check if the player's direction is defined. */
			if ((walkDirection == Direction.STOP) || (walkDirection == null)) {
				/* Set default direction to DOWN. */
				player.setDirection(Direction.DOWN);
			}

			/* Begin walking using the entity's base speed. */
			if (newSpeed != currentSpeed) {
				/* Turn on auto-walk indicator. */
				player.setAutoWalkState(true);
				player.setSpeed(newSpeed);
			}
		} else {
			/* Use the same command to stop walking. */
			/* Turn off auto-walk indicator. */
			player.setAutoWalkState(false);
			player.stop();
		}
	}
}
