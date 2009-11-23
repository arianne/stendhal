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
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.awt.Rectangle;
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
	    return nextTo(player, entity)
				&& (!isItemBelowOtherPlayer(player, entity))
				&& destInRange(player, entity, x, y)
				&& !entityCollides(player, zone, x, y, entity)
				&& (isGamblingZoneAndIsDice(entity, player) || pathToDest(player, zone, x, y, entity));
	}

    /**
     * Checks whether the player is next to the entity and provides feedback to player if not.
     *                                                                                       
     * @param player 
     *            the player doing the displacement
     * @param entity
     *            the entity being displaced
     * @return true, if next to; false otherwise
     */
    
    private boolean nextTo(final Player player, final PassiveEntity entity) {
    	if (!player.nextTo(entity)) {
    		player.sendPrivateText("You must be next to something you wish to move.");
    	}	
    	return player.nextTo(entity);
    }

	/**
	 * Checks whether the item is below <b>another</b> player and provides feedback to player.
	 * 
	 * @param player
	 *            the player doing the displacement
	 * @param entity
	 *            the entity being displaced
	 * @return true, if it cannot be taken; false otherwise
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
     * Checks whether the destination is in range and provides feedback to player if not.
     *                                                                                                                                                                                           
     * @param player                                                                                                                                                                             
     *            the player doing the displacement                                                                                                                                              
     * @param x      x-position
     * @param y      y-position
     * @return true, if in range; false otherwise                                                                                                                                       
     */

    private boolean destInRange(final Player player, final Entity entity, final int x, final int y) {
    	// Calculate from the center to make moving large items, like big corpses feel more natural
    	int centerX = (int) (x + entity.getArea().getWidth() / 2);
    	int centerY = (int) (y + entity.getArea().getHeight() / 2);
    	
    	if (!(player.squaredDistance(centerX, centerY) < EquipUtil.MAX_THROWING_DISTANCE * EquipUtil.MAX_THROWING_DISTANCE)) {
    		player.sendPrivateText("You cannot throw that far.");
    		return false;
    	} else {
    		return true;
    	}
    }
    
	/**
	 * Checks whether the destination is a collision.
	 *
	 * @param player Player attempting the move
	 * @param zone   Zone
	 * @param x      x-position
	 * @param y      y-position
	 * @param entity entity to move
	 * @return true, iff allowed
	 */
	private boolean entityCollides(final Player player, final StendhalRPZone zone, final int x, final int y, final PassiveEntity entity) {	
		if (zone.simpleCollides(entity, x, y)) {
			player.sendPrivateText("There is no space there.");		
		}
		return zone.simpleCollides(entity, x, y);
	}
	
	/**
	 * Checks whether there is a path from player to destination.
	 *
	 * @param player Player attempting the move
	 * @param zone   Zone
	 * @param x      x-position
	 * @param y      y-position
	 * @param entity entity to move
	 * @return true, iff allowed
	 */
	private boolean pathToDest(final Player player, final StendhalRPZone zone, final int x, final int y, final PassiveEntity entity) {	
		final List<Node> path = Path.searchPath(entity, zone,
				player.getX(), player.getY(), new Rectangle(x, y, 1, 1),
				64 /* maxDestination * maxDestination */, false);
		if (path.isEmpty()) {
			player.sendPrivateText("There is no easy path to that place.");		
		}
		return !path.isEmpty();
	}
	
	/* returns true if zone is semos tavern and entity is dice */
	private boolean isGamblingZoneAndIsDice(final Entity entity, final Player player) {
		final StendhalRPZone zone = player.getZone();
		return "int_semos_tavern_0".equals(zone.getName()) && ("dice").equals(entity.getTitle());
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
		new GameEvent(player.getName(), "displace", entity.get("type")).raise();

		entity.setPosition(x, y);
		entity.notifyWorldAboutChanges();
		if (entity instanceof Item) {
			final Item item = (Item) entity;
			int oldX = item.getX();
			int oldY = item.getY();
			item.onRemoveFromGround();
			item.onPutOnGround(player);
			new ItemLogger().displace(player, entity, zone, oldX, oldY, x, y);
		}
	}
}
