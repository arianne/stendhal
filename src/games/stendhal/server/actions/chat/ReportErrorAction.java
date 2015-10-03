/***************************************************************************
 *                     (C) Copyright 2015 - Stendhal                       *
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

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * handles error reports
 */
public class ReportErrorAction  implements ActionListener {
    private final Logger logger = Logger.getLogger(ReportErrorAction.class);

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!action.has(TEXT)) {
			return;
		}

		String username = PlayerEntryContainer.getContainer().get(player).username;

		// remove "context" because it contains a copy of the action with the error object.
		// Thus resulting a message with duplicated information that is hard to read
		Object context = MDC.get("context");
		MDC.remove("context");

		logger.error(player.getName() + " (" + username + "):"
		  + System.getProperty("line.separator")
		  + action.get(TEXT).replaceAll("\r\n", System.getProperty("line.separator")));

		MDC.put("context", context);
	}
}
