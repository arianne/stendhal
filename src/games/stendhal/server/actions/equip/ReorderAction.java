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
package games.stendhal.server.actions.equip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.MathHelper;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;

public class ReorderAction implements ActionListener {
	private static Logger logger = Logger.getLogger(ReorderAction.class);
	private static final List<String> reorderableSlots = Arrays.asList("bag",
			"content", "keyring", "portfolio", "spells");

	/**
	 * registers "equip" action processor.
	 */
	public static void register() {
		CommandCenter.register("reorder", new ReorderAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Entity entity = EntityHelper.getEntityFromPath(player, action.getList(EquipActionConsts.SOURCE_PATH));
		if (mayAccess(player, entity, action)) {
			reorder(entity, MathHelper.parseInt(action.get("new_position")));
		}
	}

	/**
	 * Move an entity to new position.
	 *
	 * @param entity
	 * @param newPosition desired new location in the slot
	 */
	private void reorder(Entity entity, int newPosition) {
		RPSlot slot = entity.getContainerSlot();
		if (slot.size() == 1) {
			return;
		}
		newPosition = Math.min(newPosition, slot.size() - 1);
		List<RPObject> objectsCopy = new ArrayList<RPObject>(slot.size());
		for (RPObject obj : slot) {
			objectsCopy.add(obj);
		}
		int idx = objectsCopy.indexOf(entity);
		if (idx == newPosition) {
			return;
		}
		objectsCopy.remove(entity);
		objectsCopy.add(newPosition, entity);
		slot.clear();
		for (RPObject obj : objectsCopy) {
			slot.addPreservingId(obj);
		}
		SlotOwner parent = entity.getContainerBaseOwner();
		if (parent instanceof Entity) {
			((Entity) parent).notifyWorldAboutChanges();
		}
	}

	/**
	 * Check if a player may access an entity for reordering.
	 *
	 * @param player accessing player
	 * @param entity entity to be reordered
	 * @param action reordering action
	 * @return <code>true</code> if the player may access the entity for
	 * 	reordering, otherwise <code>false</code>
	 */
	private boolean mayAccess(Player player, Entity entity, RPAction action) {
		if (entity == null) {
			return false;
		}
		RPObject object = entity.getContainer();
		if (object == null) {
			return false;
		}
		RPSlot slot = entity.getContainerSlot();
		if (!reorderableSlots.contains(slot.getName())) {
			return false;
		}
		if (slot instanceof EntitySlot) {
			if (!isReachableSlot(player, slot)) {
				return false;
			}
		} else if (object != player) {
			/*
			 * For anything else but EntitySlots (== spell slot), check that the
			 * Player is the immediate parent. The reorderableSlots check
			 * prevents messing slots that must not be accessed directly by the
			 * player.
			 */
			return false;
		}
		do {
			if (object instanceof Player) {
				if (object != player) {
					logger.error("Player " + player.getName() + " tried to reorder objects belonging to another player. Action: " + action);
					return false;
				}
			}
			slot = object.getContainerSlot();
			if ((slot != null) && !isReachableSlot(player, slot)) {
				return false;
			}

			if (object instanceof Corpse) {
				// Disable reordering corpse contents. It causes problems for
				// the automatically closing inspector windows in the client.
				return false;
			}

			object = object.getContainer();
		} while (object != null);

		return true;
	}

	/**
	 * Check the reachability of a slot.
	 *
	 * @param player
	 * @param baseSlot
	 * @return <code>true</code> if the slot is reachable, <code>false</code>
	 * 	otherwise
	 */
	private boolean isReachableSlot(final Player player, final RPSlot baseSlot) {
		if (!(baseSlot instanceof EntitySlot)) {
			return false;
		}
		EntitySlot slot = (EntitySlot) baseSlot;
		slot.clearErrorMessage();
		boolean res = slot.isReachableForTakingThingsOutOfBy(player);
		if (!res) {
			logger.debug("Unreachable slot");
			player.sendPrivateText(slot.getErrorMessage());
		}
		return res;
	}
}
