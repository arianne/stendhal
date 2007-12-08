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
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

public class AttackAction implements ActionListener {

	public static void register() {
		CommandCenter.register("attack", new AttackAction());
	}

	public void onAction(Player player, RPAction action) {

		if (action.has("target")) {
			 // evaluate the target parameter
			RPEntity entity = EntityHelper.entityFromTargetName(action.get("target"), player.getZone());

			if (entity != null)
				StendhalRPAction.startAttack(player, entity);
		}
	}
}
