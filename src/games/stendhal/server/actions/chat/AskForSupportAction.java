/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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

import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * handles asking for /support.
 */
public class AskForSupportAction  implements ActionListener {
	private final Map<String, Long> lastMsg = new HashMap<String, Long>();

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!action.has(TEXT)) {
			return;
		}

		String sender = player.getName();
		if (action.has("sender") && (player.getName().equals("postman"))) {
			sender = action.get("sender");
		}

		String text = action.get(TEXT).trim();
		if ("".equals(text)) {
			player.sendPrivateText("Usage /support <your message here>");
			return;
		}

		if (!player.getChatBucket().checkAndAdd(text.length())) {
			return;
		}

		if (Jail.isInJail(player) || GagManager.isGagged(player)) {
			// check if the player sent a support message before
			if (lastMsg.containsKey(sender)) {
				final Long timeLastMsg = System.currentTimeMillis()
						- lastMsg.get(sender);

				// the player have to wait one minute since the last support
				// message was sent
				if (timeLastMsg < 60000) {
					player.sendPrivateText("Until your sentence is over you may only send one support message per minute.");
					return;
				}
			}

			lastMsg.put(sender, System.currentTimeMillis());
		}

		final String message = action.get(TEXT)
				+ "\r\nPlease use #/supportanswer #" + sender
				+ " to answer.";

		String username = PlayerEntryContainer.getContainer().get(player).username;

		new GameEvent(sender, "support", username, action.get(TEXT)).raise();

		String temp = sender + " (" + username + ")";
		SingletonRepository.getRuleProcessor().sendMessageToSupporters(temp, message);

		player.sendPrivateText(NotificationType.SUPPORT, "You ask for support: "
				+ action.get(TEXT)
				+ "\nIt may take a little time until your question is answered.");
		player.notifyWorldAboutChanges();
	}
}
