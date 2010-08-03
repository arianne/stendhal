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

import static games.stendhal.common.constants.Actions.PUSH;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.util.Set;

import marauroa.common.game.RPAction;

/**
 * Pushes an entity which is next to the player one field forward.
 * This can be used to push afk-players away who are blocking a portal
 * for instance.
 */
public class PushAction implements ActionListener {

	public static void register() {
		final PushAction push = new PushAction();
		CommandCenter.register(PUSH, push);
	}

	public void onAction(final Player player, final RPAction action) {

		// evaluate the target parameter
		final Entity entity = EntityHelper.entityFromTargetName(
			action.get(TARGET), player);

		if ((entity == null) || !(entity instanceof RPEntity)) {
			return;
		}
		final RPEntity rpEntity = (RPEntity) entity;

		tryPush(player, rpEntity);
	}

	/**
	 * Tries to push the entity.
	 *
	 * @param player player pushing
	 * @param rpEntity entity pushed
	 */
	private void tryPush(final Player player, final RPEntity rpEntity) {
		if (canPush(player, rpEntity)) {
			final Direction dir = player.getDirectionToward(rpEntity);

			final int x = rpEntity.getX() + dir.getdx();
			final int y = rpEntity.getY() + dir.getdy();

			final StendhalRPZone zone = player.getZone();
			if (!zone.collides(rpEntity, x, y)) {
				move(player, rpEntity, x, y);
				// Stop players running toward to make trapping harder. Don't
				// stop anyone just following a path (again to make annoying
				// others harder)
				if (dir.oppositeDirection() == rpEntity.getDirection()
						&& !rpEntity.hasPath()) {
					rpEntity.stop();
				}
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
	private boolean canPush(final Player player, final RPEntity rpEntity) {

		// If object is a NPC we ignore the push action because
		// NPC don't use the pathfinder and would get confused
		// outside their fixed path. Apart from that some NPCs
		// may block a way by intention.
		if (rpEntity instanceof SpeakerNPC) {
			return false;
		}

		// prevent pushing a player off an item
		if (rpEntity instanceof Player) {
			// prevent pushing a player off an item
			final Set<Item> items = player.getZone().getItemsOnGround();
			for (final Item item : items) {
				if (rpEntity.getArea().intersects(item.getArea())) {
					player.sendPrivateText("You cannot push now because there is an item below " + rpEntity.getName() + ".");
					return false;
				}
			}
		}
		
		// players cannot push rp entities with area larger than 4
		/* I (kymara) looked in java api for Rectangle2D and couldn't find how to
		* get the value for area so i have done w * h . I guessed at .size() but didn't work 
		* if this is ok please delete these comments */
		if (rpEntity.getArea().getWidth() * rpEntity.getArea().getHeight() > 4) {
			player.sendPrivateText("You're strong, but not that strong!");
			return false;
		}

		// the number of pushes is limited per time 
		if (!player.canPush(rpEntity)) {
			player.sendPrivateText("Give yourself a breather before you start pushing again.");
			return false;
		}

		// the player must be in range. 
		return (player.nextTo(rpEntity));
	}

	/**
	 * Moves the entity to the new position.
	 *
	 * @param player   player pushing
	 * @param rpEntity entity pushed
	 * @param x new x-position
	 * @param y new y-position
	 */
	private void move(final Player player, final RPEntity rpEntity, final int x, final int y) {
		new GameEvent(player.getName(), "push", rpEntity.getName(), rpEntity.getZone().getName(), rpEntity.getX() + " " + rpEntity.getY() + " --> " + x + " " + y).raise();
		rpEntity.setPosition(x, y);
		rpEntity.notifyWorldAboutChanges();
		player.onPush(rpEntity);
	}
}
