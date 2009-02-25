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
package games.stendhal.server.actions.equip;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.util.List;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * Moving of items around on the ground.
 */
public class DisplaceAction implements ActionListener {
	private static Logger logger = Logger.getLogger(DisplaceAction.class);

	/**
	 * register this action.
	 */
	public static void register() {
		CommandCenter.register("displace", new DisplaceAction());
	}

	/**
	 * handle movement of items.
	 * @param player 
	 * @param action 
	 */
	public void onAction(final Player player, final RPAction action) {

		if (!action.has(BASEITEM) || !action.has(X) || !action.has(Y)) {
			logger.error("Incomplete DisplaceAction: " + action);
			return;
		}

		final int targetObject = action.getInt(BASEITEM);
		final StendhalRPZone zone = player.getZone();

		final Entity object = EntityHelper.entityFromZoneByID(targetObject, zone);
		if (!(object instanceof PassiveEntity)) {
			return;
		}

		final int x = action.getInt(X);
		final int y = action.getInt(Y);

		final PassiveEntity entity = (PassiveEntity) object;

		if (mayDisplace(player, zone, x, y, entity)) {
			displace(player, zone, x, y, entity);
		}
	}

	/**
	 * Checks whether this entity may be moved around on the ground.
	 *
	 * @param player Player attempting the move
	 * @param zone   Zone
	 * @param x      x-position
	 * @param y      y-position
	 * @param entity entity to move
	 * @return true, iff allowed
	 */
	private boolean mayDisplace(final Player player, final StendhalRPZone zone, final int x, final int y, final PassiveEntity entity) {
		return player.nextTo(entity)
				&& (!isItemBelowOtherPlayer(player, entity))
				&& (player.squaredDistance(x, y) < 8 * 8)
				&& !zone.simpleCollides(entity, x, y);
	}

	/**
	 * Checks whether the item is below <b>another</b> player.
	 * 
	 * @param player
	 *            the player doing the displacement
	 * @param entity
	 *            the entity beeing displaced
	 * @return true, if it cannot be take; false otherwise
	 */
	private boolean isItemBelowOtherPlayer(final Player player, final Entity entity) {
		// prevent taking of items which are below other players
		final List<Player> players = player.getZone().getPlayers();
		for (final Player otherPlayer : players) {
			if (player.equals(otherPlayer)) {
				continue;
			}
			if (otherPlayer.getArea().intersects(entity.getArea())) {
				player.sendPrivateText("You cannot take items which are below other players.");
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves an entity to a new location within the same zone.
	 *
	 * @param player the player doing the move
	 * @param zone   the zone
	 * @param x      new x-position
	 * @param y      new y-position
	 * @param entity entity to move
	 */
	private void displace(final Player player, final StendhalRPZone zone, final int x, final int y, final PassiveEntity entity) {
		SingletonRepository.getRuleProcessor().addGameEvent(
				player.getName(), "displace",
				entity.get("type"));

		entity.setPosition(x, y);
		entity.notifyWorldAboutChanges();
		if (entity instanceof Item) {
			final Item item = (Item) entity;
			item.onRemoveFromGround();
			item.onPutOnGround(player);
			new ItemLogger().displace(player, entity, zone, x, y);
		}
	}
}
