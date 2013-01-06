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

import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * checks that the target player is onlined.
 *
 * @author hendrik
 */
public class ActionTargetOnline implements ActionValidator {
	private String targetAttribute;
	private boolean tellAboutPostman;

	/**
	 * creates a new ActionTargetOnline
	 *
	 * @param targetAttribute name of attribute containing the target player name
	 * @param tellAboutPostman tell the player about postman
	 */
	public ActionTargetOnline(String targetAttribute, boolean tellAboutPostman) {
		this.targetAttribute = targetAttribute;
		this.tellAboutPostman = tellAboutPostman;
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

		if ((targetPlayer == null) || (targetPlayer.isGhost()
				&& (player.getAdminLevel() < AdministrationAction.getLevelForCommand("ghostmode")))) {
			String error = "No player named " + playerName + " is currently active.";
			if (tellAboutPostman) {
				error = error +  " Please use postman to send a message to " + playerName;
			}
			return error;
		}
		return null;
	}

}
