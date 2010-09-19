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
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * handles public said text .
 */
public class PublicChatAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (GagManager.checkIsGaggedAndInformPlayer(player)) {
			return;
		}

		if (action.has(TEXT)) {
			final String text = action.get(TEXT);
			player.put("text", text);
			new GameEvent(player.getName(), "chat",  null, Integer.toString(text.length()), text.substring(0, Math.min(text.length(), 1000))).raise();

			player.notifyWorldAboutChanges();
			SingletonRepository.getRuleProcessor().removePlayerText(player);
		}
	}

}
