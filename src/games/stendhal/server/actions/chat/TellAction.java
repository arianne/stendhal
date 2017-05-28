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

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.validator.StandardActionValidations;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * handles /tell-action (/msg-action).
 */
public class TellAction implements ActionListener {

	/**
	 * creates the full message based on the text provided by the player
	 *
	 * @param senderName   sender
	 * @param receiverName receiver
	 * @param text         text
	 * @return full message
	 */
	private String createFullMessageText(String senderName, String receiverName, String text) {
		if (senderName.equals(receiverName)) {
			return "You mutter to yourself: " + text;
		} else {
			return senderName + " tells you: " + text;
		}
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		if (!StandardActionValidations.PRIVATE_CHAT.validateAndInformPlayer(player, action)) {
			return;
		}

		String text = QuoteSpecials.quote(action.get(TEXT).trim());
		String senderName = player.getName();
		String receiverName = action.get(TARGET);


		final String message = createFullMessageText(senderName, receiverName, text);
		Player receiver = SingletonRepository.getRuleProcessor().getPlayer(receiverName);

		// transmit the message
		receiver.sendPrivateText(NotificationType.PRIVMSG, message);

		if (!senderName.equals(receiverName)) {
			player.sendPrivateText(NotificationType.PRIVMSG, "You tell " + receiverName + ": " + text);
		}

		receiver.setLastPrivateChatter(senderName);
		new GameEvent(player.getName(), "chat", receiverName, Integer.toString(text.length()), text.substring(0, Math.min(text.length(), 1000))).raise();
	}

}
