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
	private String attribute;

	/**
	 * use chat bucket
	 *
	 * @param attribute name of attribute to use for amount estimation
	 */
	public ActionSenderUseChatBucket(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * validates an RPAction.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @param data   data about this action
	 * @return <code>null</code> if the action is valid; an error message otherwise
	 */
	@Override
	public String validate(Player player, RPAction action, ActionData data) {
		int amount = 1;
		if (attribute != null) {
			String temp = action.get(attribute);
			if (temp != null) {
				amount = temp.length();
			}
		}
		if (!player.getChatBucket().checkAndAdd(amount)) {
			return ""; // empty error message to give little feedback to the spammer
		}
		return null;
	}

}
