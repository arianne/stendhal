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
package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.BASEOBJECT;
import static games.stendhal.common.constants.Actions.BASESLOT;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TARGET_PATH;
import static games.stendhal.common.constants.Actions.USE;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Uses an item or an other entity that implements Useable
 */
public class UseAction implements ActionListener {
	public static void register() {
		CommandCenter.register(USE, new UseAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		String actionZone = action.get("zone");
		// Always accept actions without the zone. Old clients send those.
		if (actionZone != null && !actionZone.equals(player.getZone().getName())) {
			return;
		}
		if (action.has(TARGET_PATH)) {
			useEntityFromPath(player, action);
		} else if (isItemInSlot(action)) {
			// When use is casted over something in a slot
			// Compatibility code
			useItemInSlot(player, action);
		} else if (action.has(TARGET)) {
			// Compatibility code
			useItemOnGround(player, action);
		}
	}

	/**
	 * Use an entity identified by TARGET_PATH.
	 *
	 * @param player
	 * @param action
	 */
	private void useEntityFromPath(Player player, RPAction action) {
		Entity entity = EntityHelper.getEntityFromPath(player, action.getList(TARGET_PATH));
		if (entity != null) {
			if (entity.isContained() && !ItemAccessPermissions.mayAccessContainedEntity(player, entity)) {
				return;
			}
			// TryUse does the owner check that mayAccessContainedEntity() does not
			tryUse(player, entity);
		}
	}

	private boolean isItemInSlot(final RPAction action) {
		return action.has(BASEITEM) && action.has(BASEOBJECT)
				&& action.has(BASESLOT);
	}

	/**
	 * Use a top level entity.
	 *
	 * @param player
	 * @param action
	 */
	private void useItemOnGround(final Player player, final RPAction action) {
		// use is cast over something on the floor
		// evaluate the target parameter
		final Entity entity = EntityHelper.entityFromTargetName(
				action.get(TARGET), player);

		if (entity != null) {
			tryUse(player, entity);
		}
	}

	/**
	 * Use an entity contained in a slot. Compatibility mode.
	 *
	 * @param player
	 * @param action
	 */
	private void useItemInSlot(final Player player, final RPAction action) {
		final Entity object = EntityHelper.entityFromSlot(player, action);
		if ((object != null) && ItemAccessPermissions.mayAccessContainedEntity(player, object)) {
			tryUse(player, object);
		}
	}

	private void tryUse(final Player player, final RPObject object) {
		if (!canUse(player, object)) {
			return;
		}

		if (object instanceof UseListener) {
			final UseListener listener = (UseListener) object;
			logUsage(player, object);
			listener.onUsed(player);
		}
	}

	private boolean canUse(final Player player, final RPObject object) {
		return !isInJailZone(player, object)
				&& !isItemBoundToOtherPlayer(player, object);
	}

	private boolean isInJailZone(final Player player, final RPObject object) {
		// HACK: No item transfer in jail (we don't want a jailed player to
		// use items like home scroll.
		final String zonename = player.getZone().getName();

		if ((object instanceof Item) && (zonename.endsWith("_jail"))) {
			player.sendPrivateText("For security reasons items may not be used in jail.");
			return true;
		}

		return false;
	}

	/**
	 * Make sure nobody uses items bound to someone else.
	 * @param player
	 * @param object
	 * @return true if item is bound false otherwise
	 */
	protected boolean isItemBoundToOtherPlayer(final Player player, final RPObject object) {
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
	 * Logs that this entity was used.
	 *
	 * @param player player using the entity
	 * @param object entity being used
	 */
	private void logUsage(final Player player, final RPObject object) {
		String name = object.get("type");
		if (object.has("name")) {
			name = object.get("name");
		}
		String infostring = "";
		if (object.has("infostring")) {
			infostring = object.get("infostring");
		}

		new GameEvent(player.getName(), USE, name, infostring).raise();
	}
}
