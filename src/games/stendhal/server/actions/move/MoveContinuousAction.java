/***************************************************************************
 *                   (C) Copyright 2003-2017 - Arianne                     *
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

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Action that allows a player to continue movement after teleport
 * via portal or after map change without the need to release and
 * press direction again.
 *
 * To enable/disable:
 *   - Execute slash command "movecont".
 *   - Change "Continuous Movement" setting in settings dialog.
 *
 * FIXME: Causes issues with portals where player is teleported
 *        to side opposite of direction pushed.
 *
 * @author AntumDeluge
 */
public class MoveContinuousAction implements ActionListener {

	/**
	 * Registers continuous movement action.
	 */
	public static void register() {
		CommandCenter.register(MOVE_CONTINUOUS, new MoveContinuousAction());
	}

	/**
	 * Enable/Disable continuous movement.
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		//String msg = null;

		if (action.has(MOVE_CONTINUOUS) && !player.has(MOVE_CONTINUOUS)) {
			player.put(MOVE_CONTINUOUS, "");
			//msg = "Continuous movement has been enabled.";
		} else if (!action.has(MOVE_CONTINUOUS) && player.has(MOVE_CONTINUOUS)) {
			player.remove(MOVE_CONTINUOUS);
			//msg = "Continuous movement has been disabled.";
		} else {
			if (player.has(MOVE_CONTINUOUS)) {
				//msg = "Continuous movement is already enabled.";
			} else {
				//msg = "Continuous movement is already disabled.";
			}
		}

		// FIXME: Notification is currently handled by client in GeneralSettings.
		//player.sendPrivateText(CLIENT, msg);
	}
}
