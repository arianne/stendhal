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
			player.sendPrivateText("使用 /support <信息内容>");
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
					player.sendPrivateText("一分钟只能发一条，请把你的问题一次性写完再发.");
					return;
				}
			}

			lastMsg.put(sender, System.currentTimeMillis());
		}

		final String message = action.get(TEXT)
				+ "\r\n请使用 #/supportanswer #" + sender
				+ " 回复.";

		String username = PlayerEntryContainer.getContainer().get(player).username;

		new GameEvent(sender, "support", username, action.get(TEXT)).raise();

		String temp = sender + " (" + username + ")";
		SingletonRepository.getRuleProcessor().sendMessageToSupporters(temp, message);

		player.sendPrivateText(NotificationType.SUPPORT, "你的申请: "
				+ action.get(TEXT)
				+ "\n正在受理，但还需要一点时间");
		player.notifyWorldAboutChanges();
	}
}
