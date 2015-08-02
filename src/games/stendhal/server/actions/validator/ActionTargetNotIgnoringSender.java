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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * checks that the target player is not ignoring the action sender.
 *
 * @author hendrik
 */
public class ActionTargetNotIgnoringSender implements ActionValidator {
	private String targetAttribute;

	/**
	 * creates a new ActionTargetNotIgnoringSender
	 *
	 * @param targetAttribute name of attribute containing the target player name
	 */
	public ActionTargetNotIgnoringSender(String targetAttribute) {
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

		// is ignored?
		final String reply = targetPlayer.getIgnore(player.getName());
		if (reply == null) {
			return null;
		}

		// sender is on ignore list
		if (reply.length() == 0) {
			return Grammar.suffix_s(playerName) + " mind is not attuned to yours, so you cannot reach them.";
		} else {
			return playerName + " is ignoring you: " + reply;
		}
	}
}
