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
package games.stendhal.server.actions;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class AttackAction implements ActionListener {


	public static void register() {
		CommandCentre.register("attack", new AttackAction());
	}

	public void onAction(Player player, RPAction action) {

		if (action.has("target")) {
			int targetObject = action.getInt("target");

			StendhalRPZone zone = player.getZone();
			RPObject.ID targetid = new RPObject.ID(targetObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);

				if (object instanceof RPEntity) {
					StendhalRPAction.startAttack(player, (RPEntity) object);
				}
			}
		}


	}
}
