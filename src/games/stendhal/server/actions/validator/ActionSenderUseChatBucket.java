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
package games.stendhal.server.actions.validator;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Uses the chat bucket
 *
 * @author hendrik
 */
public class ActionSenderUseChatBucket implements ActionValidator {

	/**
	 * validates an RPAction.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @return <code>null</code> if the action is valid; an error message otherwise
	 */
	public String validate(Player player, RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return ""; // empty error message to give little feedback to the spammer
		}
		return null;
	}

}
