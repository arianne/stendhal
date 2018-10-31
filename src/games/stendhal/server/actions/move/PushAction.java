/***************************************************************************
 *                   (C) Copyright 2003-2013 - Marauroa                    *
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

import java.awt.Point;
import java.util.Set;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.Portal;
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

	/**
	 * registers the action
	 */
	public static void register() {
		final PushAction push = new PushAction();
		CommandCenter.register(PUSH, push);
	}

	@Override
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
	 * @param pusher player pushing
	 * @param pushed entity pushed
	 */
	private void tryPush(final Player pusher, final RPEntity pushed) {
		if (canPush(pusher, pushed)) {
			final Direction dir = pusher.getDirectionToward(pushed);

			final Point prevPos = new Point(pushed.getX(), pushed.getY());

			final int x = prevPos.x + dir.getdx();
			final int y = prevPos.y + dir.getdy();

			final StendhalRPZone zone = pusher.getZone();
			if (!zone.collides(pushed, x, y)) {
				move(pusher, pushed, x, y);
				// Stop players running toward to make trapping harder. Don't
				// stop anyone just following a path (again to make annoying
				// others harder).
				if ((dir.oppositeDirection() == pushed.getDirection())
						&& !pushed.hasPath()) {
					pushed.stop();
				}
				if (!(pushed instanceof Player)) {
					/*
					 * Clear path of creatures, so that they know to get a new
					 * one after the one followed has been broken. Has the nice
					 * side effect that it's possible to make a creature stop
					 * chasing someone, and pick a new target.
					 * ("Hey you, try chasing me instead!")
					 */
					pushed.setPath(null);
				}

				// check for portals in new position
				for (final Entity areaEntity : zone.getEntitiesAt(x, y)) {
					if (areaEntity instanceof Portal) {
						((Portal) areaEntity).onPushedOntoFrom(pushed, pusher, prevPos);
					}
				}
			}
		}
	}

	/**
	 * Can this push be done according to the rules?
	 *
	 * @param pusher player pushing
	 * @param pushed entity pushed
	 * @return true, if the push is possible, false otherwise
	 */
	private boolean canPush(final Player pusher, final RPEntity pushed) {

		// If object is a NPC we ignore the push action because
		// NPC don't use the pathfinder and would get confused
		// outside their fixed path. Apart from that some NPCs
		// may block a way by intention.
		if (pushed instanceof SpeakerNPC) {
			return false;
		}

		// players cannot push rp entities with area larger than 4
		if ((pushed.getArea().getWidth() * pushed.getArea().getHeight()) > 4) {
			pusher.sendPrivateText("You're strong, but not that strong!");
			return false;
		}

		// the number of pushes is limited per time
		if (!pusher.canPush(pushed)) {
			pusher.sendPrivateText("Give yourself a breather before you start pushing again.");
			return false;
		}

		// the player must be in range.
		return (pusher.nextTo(pushed));
	}

	/**
	 * Moves the entity to the new position.
	 *
	 * @param pusher player pushing
	 * @param pushed entity pushed
	 * @param x new x-position
	 * @param y new y-position
	 */
	private void move(final Player pusher, final RPEntity pushed, final int x, final int y) {

		// move items under players, with the players, when pushed
		if (pushed instanceof Player) {
			final Set<Item> items = pusher.getZone().getItemsOnGround();
			for (final Item item : items) {
				if (pushed.getArea().intersects(item.getArea())) {
					String boundTo = item.getBoundTo();
					// Move bound items only if they belong to the pushed player
					if ((boundTo != null) && !pushed.getName().equals(boundTo)) {
						continue;
					}
					item.setPosition(x, y);
					item.notifyWorldAboutChanges();
					// log the item ground-to-ground displacement
					// but who caused it to move, the pusher or the pushed?
					// currently the source of the ground-to-ground movement of the item is set as the pusher
					new ItemLogger().displace(pusher, item, pusher.getZone(), pushed.getX(), pushed.getY(), x, y);
				}
			}
		}

		new GameEvent(pusher.getName(), "push", pushed.getName(), pushed.getZone().getName(), pushed.getX() + " " + pushed.getY() + " --> " + x + " " + y).raise();
		pushed.setPosition(x, y);
		pushed.notifyWorldAboutChanges();
		pusher.onPush(pushed);
	}
}
