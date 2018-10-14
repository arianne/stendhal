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
package games.stendhal.server.actions;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Default action if the client sends something the server does not understand.
 *
 * @author hendrik
 */
class UnknownAction implements ActionListener {
	private static Logger logger = Logger.getLogger(UnknownAction.class);
	private final List<String> suggestions;

	/**
	 * Create an UnknownAction with no suggestions for commands.
	 */
	UnknownAction() {
		suggestions = null;
	}

	/**
	 * Create an UnknownAction with suggestions for possibly intended commands.
	 *
	 * @param suggestions
	 */
	UnknownAction(List<String> suggestions) {
		Collections.sort(suggestions);
		this.suggestions = suggestions;
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		String type = "null";
		if (action != null) {
			type = action.get("type");
		}
		logger.warn(player + " tried to execute unknown action " + type);
		if (player != null) {
			StringBuilder msg = new StringBuilder("Unknown command /");
			msg.append(type);
			if (suggestions != null) {
				int size = suggestions.size();
				msg.append(". Did you mean");
				if (size == 1) {
					msg.append(" #/");
					msg.append(suggestions.get(0));
				}
				if (size > 1) {
					for (int i = 0; i < (size - 2); i++) {
						msg.append(" #/");
						msg.append(suggestions.get(i));
						msg.append(",");
					}
					msg.append(" #/");
					msg.append(suggestions.get(size - 2));
					msg.append(" or #/");
					msg.append(suggestions.get(size - 1));
				}
				msg.append("? Or type #/help to get a list.");
			} else {
				msg.append(". Please type #/help to get a list.");
			}
			player.sendPrivateText(NotificationType.ERROR, msg.toString());
		}
	}
}
