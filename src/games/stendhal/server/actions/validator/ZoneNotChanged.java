/***************************************************************************
 *                   (C) Copyright 2003-2014 - Faiumoni                    *
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
 * Checks that the player is on the same zone, as indicated by the zone
 * information of the action.
 */
public class ZoneNotChanged implements ActionValidator {
	@Override
	public String validate(Player player, RPAction action, ActionData data) {
		String actionZone = action.get("zone");
		// Always accept actions without the zone. Old clients send those.
		if (actionZone == null || actionZone.equals(player.getZone().getName())) {
				return null;
		}
		return "";
	}
}
