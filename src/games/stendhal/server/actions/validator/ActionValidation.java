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

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * validates an RPAction using a list of ActionValidators
 *
 * @author hendrik
 */
public class ActionValidation implements ActionValidator {
	private final List<ActionValidator> validators = new LinkedList<ActionValidator>();

	/**
	 * adds an ActionValidator
	 *
	 * @param validator ActionValidator
	 */
	public void add(ActionValidator validator) {
		validators.add(validator);
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
		for (ActionValidator validator : validators) {
			String res = validator.validate(player, action, data);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	/**
	 * validates an RPAction.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @return <code>null</code> if the action is valid; an error message otherwise
	 */
	public String validate(Player player, RPAction action) {
		return validate(player, action, null);
	}


	/**
	 * validates an RPAction and tells the player about validation issues.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @return true, if the action may continue; false on error
	 */
	public boolean validateAndInformPlayer(Player player, RPAction action) {
		String error = validate(player, action, null);
		if ((error != null) && !error.trim().equals("")) {
			tellIgnorePostman(player, error);
		}
		return error == null;
	}


	/**
	 * validates an RPAction and tells the player about validation issues.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @param data action datra
	 * @return true, if the action may continue; false on error
	 */
	public boolean validateAndInformPlayer(Player player, RPAction action, ActionData data) {
		String error = validate(player, action, data);
		if ((error != null) && !error.trim().equals("")) {
			tellIgnorePostman(player, error);
		}
		return error == null;
	}

	/**
	 * send a message to a player, unless that is postman
	 *
	 * @param receiver receiver of the message
	 * @param message message
	 */
	private void tellIgnorePostman(final Player receiver, final String message) {
		if (!receiver.getName().equals("postman")) {
			receiver.sendPrivateText(message);
		}
	}
}
