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
import games.stendhal.server.actions.validator.StandardActionValidations;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.RPAction;

/**
 * handles publicly said text .
 */
public class PublicChatAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!StandardActionValidations.CHAT.validateAndInformPlayer(player, action)) {
			return;
		}

		String text = QuoteSpecials.quote(action.get(TEXT));
		new GameEvent(player.getName(), "chat",  null, Integer.toString(text.length()), text.substring(0, Math.min(text.length(), 1000))).raise();

		if (player.getStatusList().countStatusByType(StatusType.DRUNK) >= 2) {
			text = text + " *hicks*";
		}
		player.put("text", text);

		player.notifyWorldAboutChanges();
		SingletonRepository.getRuleProcessor().removePlayerText(player);
	}

}
