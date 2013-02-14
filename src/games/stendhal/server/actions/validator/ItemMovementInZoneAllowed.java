/***************************************************************************
 *                    (C) Copyright 2013 - Faiumoni e. V.                  *
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
 * checks that the zone allows item movement.
 *
 * @author hendrik
 */
public class ItemMovementInZoneAllowed  implements ActionValidator {

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
		String noItemMovementMessage = player.getZone().getNoItemMoveMessage();
		return noItemMovementMessage;
	}

}
