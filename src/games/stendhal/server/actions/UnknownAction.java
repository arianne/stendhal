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
package games.stendhal.server.actions;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * Default action if the client sends something the server does not understand.
 *
 * @author hendrik
 */
class UnknownAction implements ActionListener {
	private static Logger logger = Logger.getLogger(UnknownAction.class);

	public void onAction(final Player player, final RPAction action) {
		String type = "null";
		if (action != null) {
			type = action.get("type");
		}
		logger.warn(player + " tried to execute unknown action " + type);
		if (player != null) {
			player.sendPrivateText(NotificationType.ERROR,
					"Unknown command " + type + ". Please type /help to get a list.");
		}
	}
}