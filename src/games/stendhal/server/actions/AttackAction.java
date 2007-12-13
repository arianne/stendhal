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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
public class AttackAction implements ActionListener {

	
	private static final String _ATTACK = "attack";

	public static void register() {
		CommandCenter.register(_ATTACK, new AttackAction());
	}

	public void onAction(Player player, RPAction action) {

		if (action.has(TARGET)) {
			// evaluate the target parameter
			Entity entity = EntityHelper.entityFromTargetName(action
					.get(TARGET), player.getZone());

			if (entity instanceof RPEntity) {
				StendhalRPAction.startAttack(player, (RPEntity) entity);
			}
		}
	}
}
