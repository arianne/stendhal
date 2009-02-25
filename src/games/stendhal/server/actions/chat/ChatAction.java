/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import static games.stendhal.common.constants.Actions.ANSWER;
import static games.stendhal.common.constants.Actions.CHAT;
import static games.stendhal.common.constants.Actions.EMOTE;
import static games.stendhal.common.constants.Actions.SUPPORT;
import static games.stendhal.common.constants.Actions.TELL;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.RPAction;

/**
 * Processes /chat, /tell (/msg) and /support.
 */
public class ChatAction {


	/**
	 * Registers AnswerAction ChatAction TellAction and SupportAction.
	 */
	public static void register() {
		CommandCenter.register(ANSWER, new AnswerAction());
		CommandCenter.register(CHAT, new PublicChatAction());
		CommandCenter.register(TELL, new TellAction());
		CommandCenter.register(SUPPORT, new AskForSupportAction());
		CommandCenter.register(EMOTE, new EmoteAction());
	}

	public void onAction(final Player player, final RPAction action) {

		if (GagManager.isGagged(player)) {
			final long timeRemaining = SingletonRepository.getGagManager().getTimeRemaining(player);
			player.sendPrivateText("You are gagged, it will expire in "
					+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
			return;
		}
	}
}
