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
package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import static games.stendhal.common.constants.Actions.TYPE;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * handles /tell-action (/msg-action).
 */
class AnswerAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(TEXT)) {
			if (player.getLastPrivateChatter() != null) {
				// convert the action to a /tell action
				action.put(TYPE, "tell");
				action.put(TARGET, player.getLastPrivateChatter());
				new TellAction().onAction(player, action);
			} else {
				player.sendPrivateText("Nobody has talked privately to you.");
			}
		}
	}

}
