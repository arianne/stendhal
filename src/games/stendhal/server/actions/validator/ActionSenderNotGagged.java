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
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.RPAction;

/**
 * checks that the player is not gagged.
 *
 * @author hendrik
 */
public class ActionSenderNotGagged implements ActionValidator {

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
		if (GagManager.isGagged(player)) {
			long timeRemaining = SingletonRepository.getGagManager().getTimeRemaining(player);
			return "You are gagged, it will expire in "
			+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L));
		}
		return null;
	}

}
