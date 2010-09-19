/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPAction;

/**
 * handles asking for /support.
 */
public class AskForSupportAction  implements ActionListener {
	private final Map<String, Long> lastMsg = new HashMap<String, Long>();

	public void onAction(final Player player, final RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (action.has(TEXT)) {

			if ("".equals(action.get(TEXT).trim())) {
				player.sendPrivateText("Usage /support <your message here>");
				return;
			}

			if (Jail.isInJail(player) || GagManager.isGagged(player)) {
				// check if the player sent a support message before
				if (lastMsg.containsKey(player.getName())) {
					final Long timeLastMsg = System.currentTimeMillis()
							- lastMsg.get(player.getName());

					// the player have to wait one minute since the last support
					// message was sent
					if (timeLastMsg < 60000) {
						player.sendPrivateText("Until your sentence is over you may only send one support message per minute.");
						return;
					}
				}

				lastMsg.put(player.getName(), System.currentTimeMillis());
			}

			final String message = action.get(TEXT)
					+ "\r\nPlease use #/supportanswer #" + player.getTitle()
					+ " to answer.";

			new GameEvent(player.getName(), "support", player.getName(), action.get(TEXT)).raise();

			SingletonRepository.getRuleProcessor().sendMessageToSupporters(player.getTitle(), message);

			player.sendPrivateText("You ask for support: "
					+ action.get(TEXT)
					+ "\nIt may take a little time until your question is answered.");
			player.notifyWorldAboutChanges();
		}
	}
}
