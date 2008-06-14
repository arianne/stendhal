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

/**
 * Pushes an entity which is next to the player one field forward.
 * This can be used to push afk-players away who are blocking a portal
 * for instance.
 */
public class PushAction implements ActionListener {
	private static final String _PUSH = "push";

	public static void register() {
		PushAction push = new PushAction();
		CommandCenter.register(_PUSH, push);
	}

	public void onAction(Player player, RPAction action) {

		// evaluate the target parameter
		Entity entity = EntityHelper.entityFromTargetName(
			action.get(TARGET), player);

		if ((entity == null) || !(entity instanceof RPEntity)) {
			return;
		}
		RPEntity rpEntity = (RPEntity) entity;

		tryPush(player, rpEntity);
	}

	/**
	 * Tries to push the entity.
	 *
	 * @param player player pushing
	 * @param rpEntity entity pushed
	 */
	private void tryPush(Player player, RPEntity rpEntity) {
		if (canPush(player, rpEntity)) {
			Direction dir = player.getDirectionToward(rpEntity);

			int x = rpEntity.getX() + dir.getdx();
			int y = rpEntity.getY() + dir.getdy();

			StendhalRPZone zone = player.getZone();
			if (!zone.collides(rpEntity, x, y)) {
				move(player, rpEntity, x, y);
			}
		}
	}

	/**
	 * Can this push be done according to the rules?
	 *
	 * @param player   player pushing
	 * @param rpEntity entity pushed
	 * @return true, if the push is possible, false otherwise
	 */
	private boolean canPush(Player player, RPEntity rpEntity) {

		// If object is a NPC we ignore the push action because
		// NPC don't use the pathfinder and would get confused
		// outside there fixed path. Apart from that some NPCs
		// may block a way by intend.
		if (rpEntity instanceof SpeakerNPC) {
			return false;
		}

		// the number of pushes is limited per time and the player
		// must be in range
		return (player.canPush(rpEntity) && player.nextTo(rpEntity));
	}

	/**
	 * Moves the entity to the new position.
	 *
	 * @param player   player pusing
	 * @param rpEntity entity pushed
	 * @param x new x-position
	 * @param y new y-position
	 */
	private void move(Player player, RPEntity rpEntity, int x, int y) {
		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
			"push", rpEntity.getName(), rpEntity.getZone().getName(), 
			rpEntity.getX() + " " + rpEntity.getY() + " --> " + x + " " + y);
		rpEntity.setPosition(x, y);
		rpEntity.notifyWorldAboutChanges();
		player.onPush(rpEntity);
	}
}
