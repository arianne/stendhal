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

import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * removes a buddy from the friends list
 */
class RemoveBuddyAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);
			int removed = 0;

			for(String name : player.getBuddies()) {
				// search for buddy names using case insensitive matching
				if (name.equalsIgnoreCase(who)) {
					if (player.removeBuddy(name)) {
						new GameEvent(player.getName(), "buddy", "remove", name).raise();
						player.sendPrivateText(name + " was removed from your buddy list.");

						++removed;
					}
				}
			}

			if (removed == 0) {
				player.sendPrivateText("There is no \"" + who + "\" in your buddy list.");
			}
		}

		new BuddyCleanup(player).cleanup();
	}

}
