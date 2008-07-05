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
package games.stendhal.server.actions;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants._BASEITEM;
import static games.stendhal.server.actions.WellKnownActionConstants._BASEOBJECT;
import static games.stendhal.server.actions.WellKnownActionConstants._BASESLOT;
import static games.stendhal.server.actions.WellKnownActionConstants._USE;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class UseAction implements ActionListener {

	public static void register() {
		CommandCenter.register(_USE, new UseAction());
	}

	public void onAction(Player player, RPAction action) {

		// When use is casted over something in a slot
		if (isItemInSlot(action)) {
			useItemInSlot(player, action);
		} else if (action.has(TARGET)) {
			useItemOnGround(player, action);
		}
	}

	private boolean isItemInSlot(RPAction action) {
		return action.has(_BASEITEM) && action.has(_BASEOBJECT)
				&& action.has(_BASESLOT);
	}

	private void useItemOnGround(Player player, RPAction action) {
		// use is cast over something on the floor
		// evaluate the target parameter
		Entity entity = EntityHelper.entityFromTargetName(
				action.get(TARGET), player);

		if (entity != null) {
			tryUse(player, entity);
		}
	}

	private void useItemInSlot(Player player, RPAction action) {
		Entity object = EntityHelper.entityFromSlot(player, action);
		if ((object != null) && canAccessSlot(player, object.getBaseContainer())) {
			tryUse(player, object);
		}
	}

	private boolean canAccessSlot(Player player, RPObject base) {
		if (!((base instanceof Player) || (base instanceof Corpse) || (base instanceof Chest))) {
			// Only allow to use objects from players, corpses or chests
			return false;
		}

		if ((base instanceof Player)
				&& !player.getID().equals(base.getID())) {
			// Only allowed to use item of our own player.
			return false;
		}
		
		return true;
	}

	private void tryUse(Player player, RPObject object) {

		if (!canUse(player, object)) {
			return;
		}

		logUsage(player, object);

		if (object instanceof UseListener) {
			UseListener listener = (UseListener) object;
			listener.onUsed(player);
		}
	}

	private boolean canUse(Player player, RPObject object) {
		return !isInJailZone(player, object) 
			&& !isItemBoundToOtherPlayer(player, object);
	}

	private boolean isInJailZone(Player player, RPObject object) {
		// HACK: No item transfer in jail (we don't want a jailed player to
		// use items like home scroll.
		String zonename = player.getZone().getName();

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
	 * @return true if item is bound flase otherwise
	 */
	protected boolean isItemBoundToOtherPlayer(Player player, RPObject object) {
		if (object instanceof Item) {
			Item item = (Item) object;
			if (item.isBound() && !item.isBoundTo(player)) {
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
	private void logUsage(Player player, RPObject object) {
		String name = object.get("type");
		if (object.has("name")) {
			name = object.get("name");
		}
		String infostring = "";
		if (object.has("infostring")) {
			infostring = object.get("infostring");
		}

		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), _USE,
				name, infostring);
		
		// TODO: log to itemlog, too?
	}
}
