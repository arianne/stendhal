/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.REASON;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * enables a player to mark himself/herself as grumpy to prevent messages from non-friends.
 *
 * @author hendrik
 */
public class GrumpyAction implements ActionListener {

	/**
	 * Handle a Grumpy action.
	 *
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(REASON)) {
			player.setGrumpyMessage(action.get(REASON));
		} else {
			player.setGrumpyMessage(null);
		}
		player.notifyWorldAboutChanges();

	}

}
