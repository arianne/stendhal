/*
 * @(#) src/games/stendhal/server/actions/AwayAction.java
 *
 * $Id$
 */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.AWAY;
import static games.stendhal.common.constants.Actions.MESSAGE;
import static games.stendhal.common.constants.Actions.TYPE;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.ReadPostmanMessages;
import marauroa.common.game.RPAction;

/**
 * Process /away commands.
 */
public class AwayAction implements ActionListener {

	/**
	 * Registers AwayAction with its trigger word "away".
	 */
	public static void register() {
		CommandCenter.register(AWAY, new AwayAction());
	}

	/**
	 * changes away status depending on existence of MESSAGE in action.
	 *
	 * If action contains MESSAGE, the away status is set else the away status
	 * is unset.
	 *
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		if (AWAY.equals(action.get(TYPE))) {
			if (action.has(MESSAGE)) {
				player.setAwayMessage(QuoteSpecials.quote(
						"\"" + action.get(MESSAGE) + "\""));
			} else {
				player.setAwayMessage(null);
				// get the postman messages you might have received when you were away
				new ReadPostmanMessages().readMessages(player);
			}

			player.notifyWorldAboutChanges();
		}
	}
}
