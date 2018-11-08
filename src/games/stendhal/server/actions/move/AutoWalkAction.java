/***************************************************************************
 *                   (C) Copyright 2003-2015 - Arianne                     *
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

import static games.stendhal.common.constants.Actions.AUTOWALK;
import static games.stendhal.common.constants.Actions.MODE;
import static games.stendhal.common.constants.Actions.WALK;
import static games.stendhal.common.constants.General.PATHSET;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.RPAction;

/**
 * Action that sets character speed and begins moving in faced direction. If
 * the action is executed with the MODE attribute set to "stop" it will
 * exclusively stop the entity's movement and set auto-walking off. Otherwise
 * it will toggle auto-walk.
 *
 * To enable:
 * - Execute slash command "/walk".
 * - Right-click player and select "walk".
 * - Hold "Alt" key and press any direction key.
 * To disable:
 * - Execute slash command "/walk" or "/stopwalk".
 * - Right-click player and select "stop".
 * - Press the arrow key in the direction that the player is currently walking.
 * - Click any area of the map that is walkable.
 * - Walk into a collision tile (colliding with other entities will not disable auto-walk).
 *
 * @author AntumDeluge
 */
public class AutoWalkAction implements ActionListener {

	/**
	 * Registers walk action.
	 */
	public static void register() {
		CommandCenter.register(WALK, new AutoWalkAction());
	}

	/**
	 * Begin walking.
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {

		/* Clear player's path if set. */
		if (player.hasPath()) {
			player.clearPath();
		}
		if (player.has(PATHSET)) {
			player.remove(PATHSET);
		}

		/* The speed at which the player will walk. */
		final double currentSpeed = player.getSpeed();
		final double newSpeed = player.getBaseSpeed();
		final Direction walkDirection = player.getDirection();

		final String mode = action.get(MODE);

		/* Player used "/stop" slash command. */
		if (mode != null && mode.equals("stop")) {
			if (!player.stopped()) {
				/* Remove AUTOWALK before calling stop(). */
				if (player.has(AUTOWALK)) {
					player.remove(AUTOWALK);
				}
				player.stop();
				return;
			} else {
				return;
			}
		}

		if (player.stopped()) {
			/* Check if the player's direction is defined. */
			if ((walkDirection == Direction.STOP) || (walkDirection == null)) {
				/* Set default direction to DOWN. */
				player.setDirection(Direction.DOWN);
			}

			/* Check if player is has a debilitating status effect */
			// FIXME: How to stop player when poised while using auto walk?
			if (player.hasStatus(StatusType.POISONED)
					|| player.hasStatus(StatusType.CONFUSED)) {
				player.sendPrivateText("You are disoriented and you cannot move normally. You only seem able to walk backwards and cannot plan out any route in advance.");
				if (player.has(AUTOWALK)) {
					player.remove(AUTOWALK);
				}
				return;
			}

			/* Begin walking using the entity's base speed. */
			if (newSpeed != currentSpeed) {
				/* Turn on AUTOWALK before calling setSpeed(). */
				if (!player.has(AUTOWALK)) {
					player.put(AUTOWALK, "");
				}
				player.setSpeed(newSpeed);
			}
		} else {
			/* Use the same command to stop walking. */
			/* Turn off auto-walk indicator. */
			if (player.has(AUTOWALK)) {
				player.remove(AUTOWALK);
			}
			player.stop();
		}
	}
}
