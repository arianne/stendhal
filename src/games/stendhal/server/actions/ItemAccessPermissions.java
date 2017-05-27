/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Utilities for checking permissions to access items.
 */
public class ItemAccessPermissions {
	/**
	 * Check if a player may access the location of a contained  item. Note that
	 * access rights to the item itself is <em>not</em> checked.
	 *
	 * @param player
	 * @param entity
	 * @return <code>true</code> if the player may access items in the location
	 * of item
	 */
	public static boolean mayAccessContainedEntity(Player player, Entity entity) {
		RPObject parent = entity.getContainer();
		while ((parent != null) && (parent != entity)) {
			EntitySlot slot = getContainingSlot(entity);
			if ((slot == null) || !slot.isReachableForTakingThingsOutOfBy(player)) {
				return false;
			}
			if (parent instanceof Item) {
				entity = (Item) parent;
				if (isItemBoundToOtherPlayer(player, entity)) {
					return false;
				}
				parent = entity.getContainer();
			} else if (parent instanceof Corpse) {
				Corpse corpse = (Corpse) parent;
				if (!corpse.mayUse(player)) {
					player.sendPrivateText("Only " + corpse.getCorpseOwner() + " may access the corpse for now.");
					return false;
				}
				// Corpses are top level objects
				return true;
			} else if (parent instanceof Player) {
				// Only allowed to use item of our own player.
				return player == parent;
			} else if (parent instanceof Chest) {
				// No bound chests
				return true;
			} else {
				// Only allow to use objects from players, corpses, chests or
				// containing items
				return false;
			}
		}

		return true;
	}

	/**
	 * Make sure nobody uses items bound to someone else. This also notifies
	 * the player trying to use the item, if it is not allowed.
	 *
	 * @param player
	 * @param object
	 * @return true if item is bound false otherwise
	 */
	static boolean isItemBoundToOtherPlayer(final Player player, final RPObject object) {
		if (object instanceof Item) {
			final Item item = (Item) object;
			if (item.isBound() && !player.isBoundTo(item)) {
				player.sendPrivateText("This "
						+ item.getName()
						+ " is a special reward for " + item.getBoundTo()
						+ ". You do not deserve to use it.");
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the slot containing an entity.
	 *
	 * @param entity
	 * @return containing slot
	 */
	private static EntitySlot getContainingSlot(Entity entity) {
		RPSlot slot = entity.getContainerSlot();
		if (slot instanceof EntitySlot) {
			return (EntitySlot) slot;
		}
		return null;
	}
}
