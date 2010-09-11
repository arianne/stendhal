/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.List;


/**
 * a pseudo slot which represents a location on the ground
 *
 * @author hendrik
 */
public class GroundSlot extends EntitySlot {
	private StendhalRPZone zone;
	private int itemid;
	private Item item;
	private int x;
	private int y;

	/**
	 * creates a new GroundSlot
	 *
	 * @param zone zone
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public GroundSlot(StendhalRPZone zone, int x, int y) {
		this.zone = zone;
		this.x = x;
		this.y = y;
	}

	/**
	 * creates a new GroundSlot with an item.
	 * @param zone zone
	 * @param item item on the ground
	 */
	public GroundSlot(StendhalRPZone zone, Item item) {
		this.zone = zone;
		this.item = item;
		this.x = item.getX();
		this.y = item.getY();
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		// TODO do range check
		return !isItemBelowOtherPlayer(entity);
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		// TODO do range check
		return true;
	}

	/**
	 * Checks whether the item is below <b>another</b> player.
	 * 
	 * @param sourceItem to check
	 * @return true, if it cannot be taken; false otherwise
	 */
	private boolean isItemBelowOtherPlayer(final Entity player) {
		if (item == null) {
			return false;
		}
		final List<Player> players = player.getZone().getPlayers();
		for (final Player otherPlayer : players) {
			if (player.equals(otherPlayer)) {
				continue;
			}
			if (otherPlayer.getArea().intersects(item.getArea())) {
				return true;
			}
		}
		return false;
	}
}
