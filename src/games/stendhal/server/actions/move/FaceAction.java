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
import static games.stendhal.common.constants.Actions.FACE;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * looks into the specified direction without moving
 */
public class FaceAction implements ActionListener {

	/**
	 * register the action
	 */
	public static void register() {
		CommandCenter.register(FACE, new FaceAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		if (action.has(DIR)) {
			player.stop();
			player.setDirection(Direction.build(action.getInt(DIR)));
			player.notifyWorldAboutChanges();
		}

	}
}
