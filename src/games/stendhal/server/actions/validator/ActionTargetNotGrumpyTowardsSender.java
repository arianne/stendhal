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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * checks that the target player is not grumpy towards the action sender.
 *
 * @author hendrik
 */
public class ActionTargetNotGrumpyTowardsSender implements ActionValidator {
	private String targetAttribute;

	/**
	 * creates a new ActionTargetNotGrumpyTowardsSender
	 *
	 * @param targetAttribute name of attribute containing the target player name
	 */
	public ActionTargetNotGrumpyTowardsSender(String targetAttribute) {
		this.targetAttribute = targetAttribute;
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
		String playerName = action.get(targetAttribute);
		Player targetPlayer = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		final String grumpy = targetPlayer.getGrumpyMessage();
		if (grumpy == null) {
			return null;
		}

		// the target is grumpy, check if the sender is a friend
		if (!targetPlayer.containsKey("buddies", player.getName())) {
			if (grumpy.length() == 0) {
				return playerName + " has a closed mind, and is seeking solitude from all but close friends";
			} else {
				return playerName + " is seeking solitude from all but close friends: " + grumpy;
			}
		}

		return null;
	}
}
