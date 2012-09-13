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
 * checks that the target player is not away.
 *
 * @author hendrik
 */
public class ActionTargetNotAway implements ActionValidator {
	private String targetAttribute;

	/**
	 * creates a new ActionTargetNotAway
	 *
	 * @param targetAttribute name of attribute containing the target player name
	 */
	public ActionTargetNotAway(String targetAttribute) {
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
	public String validate(Player player, RPAction action, ActionData data) {
		String playerName = action.get(targetAttribute);
		Player targetPlayer = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		String awayMessage = targetPlayer.getAwayMessage();
		if (awayMessage != null) {
			return targetPlayer.getName() + " is away from keyboard: " + awayMessage;
		}
		return null;
	}

}
