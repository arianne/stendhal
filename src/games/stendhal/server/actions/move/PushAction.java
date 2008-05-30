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
package games.stendhal.server.actions.move;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

public class PushAction implements ActionListener {
	private static final String _PUSH = "push";


	public static void register() {
		PushAction push = new PushAction();
		CommandCenter.register(_PUSH, push);
	}

	public void onAction(Player player, RPAction action) {
		if (!action.has(TARGET)) {
			return;
		}

		// evaluate the target parameter
		StendhalRPZone zone = player.getZone();
		Entity entity = EntityHelper.entityFromTargetName(
			action.get(TARGET), player);

		if ((entity == null) || !(entity instanceof RPEntity)) {
			return;
		}

		RPEntity rpEntity = (RPEntity) entity;

		// If object is a NPC we ignore the push action.
		if (rpEntity instanceof SpeakerNPC) {
			return;
		}

		if (player.canPush(rpEntity) && player.nextTo(rpEntity)) {
			Direction dir = player.getDirectionToward(rpEntity);

			int x = rpEntity.getX() + dir.getdx();
			int y = rpEntity.getY() + dir.getdy();

			if (!zone.collides(rpEntity, x, y)) {
				SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					"push", rpEntity.getName(), rpEntity.getZone().getName(), 
					rpEntity.getX() + " " + rpEntity.getY() + " --> " + x + " " + y);
				rpEntity.setPosition(x, y);
				rpEntity.notifyWorldAboutChanges();
				player.onPush(rpEntity);
			}
		}
	}
}
