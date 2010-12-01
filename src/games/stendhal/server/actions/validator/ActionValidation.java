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

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPAction;

/**
 * validates an RPAction using a list of ActionValidators
 *
 * @author hendrik
 */
public class ActionValidation {
	private List<ActionValidator> validators = new LinkedList<ActionValidator>();

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
	 * @return <code>null</code> if the action is valid; an error message otherwise
	 */
	public String validate(Player player, RPAction action) {
		for (ActionValidator validator : validators) {
			String res = validator.validate(player, action);
			if (res != null) {
				return res;
			}
		}
		return null;
	}
}
