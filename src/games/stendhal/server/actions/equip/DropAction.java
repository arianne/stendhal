/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import java.awt.Rectangle;
import java.util.List;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class DropAction extends EquipmentAction {
	public static void register() {
		CommandCenter.register("drop", new DropAction());
	}

	@Override
	protected void execute(final Player player, final RPAction action, final SourceObject source) {

		// get destination and check it
		final DestinationObject dest = new DestinationObject(action, player);
		if (!dest.checkDistance(player, EquipUtil.MAX_THROWING_DISTANCE)) {
			player.sendPrivateText("You cannot throw that far.");
			return;
		}

		if (!dest.isValid() || !dest.checkClass(validContainerClassesList)) {
			logger.warn("destination is invalid. action is: " + action);
			// destination is not valid
			return;
		}

		if (!mayDrop(player, action, source.getEntity())) {
			return;
		}

		final Entity entity = source.getEntity();
		final String itemName = source.getEntityName();

		if (source.moveTo(dest, player)) {
			if (entity instanceof Item) {
				final Item item = (Item) entity;

				if (item.isBound()) {
					player.sendPrivateText("You put a valuable item on the ground. Please note that it will expire in "
							+ (Item.DEGRADATION_TIMEOUT / 60)
							+ " minutes, as all items do. But in this case there is no way to restore it.");
				}
			}

			final int amount = source.getQuantity();
			new GameEvent(player.getName(), "drop", itemName, source.getSlot(), dest.getSlot(), Integer.toString(amount)).raise();
			player.updateItemAtkDef();
		}
	}

	/**
	 * Checks if an item may be dropped from player's bag.
	 *
	 * @param player
	 * @param action
	 * @param entity
	 * @return
	 */
	private boolean mayDrop(final Player player, final RPAction action, final Entity entity) {
		// FIXME: Which is better?
		//final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(action.get("zoneid"));
		final StendhalRPZone zone = player.getZone();
		if (action.has(EquipActionConsts.GROUND_X) && action.has(EquipActionConsts.GROUND_Y)) {
			final int x = action.getInt(EquipActionConsts.GROUND_X);
			final int y = action.getInt(EquipActionConsts.GROUND_Y);

			return isTargetOccupiable(player, zone, x, y) && pathToDest(player, zone, x, y, entity);
		}

		return false;
	}

	/**
	 * Checks for a clear path from player to target position.
	 *
	 * @param player
	 * 		Player that is dropping the item.
	 * @param zone
	 * 		Zone where item is dropped.
	 * @param x
	 * 		Horizontal coordinate of target position.
	 * @param y
	 * 		Vertical coordinate of target position.
	 * @param entity
	 * @return
	 * 		<code>true</code> if there is a clear path to target position.
	 */
	private boolean pathToDest(final Player player, final StendhalRPZone zone, final int x, final int y, final Entity entity) {
		final List<Node> path = Path.searchPath(entity, zone,
				player.getX(), player.getY(), new Rectangle(x, y, 1, 1),
				64 /* maxDestination * maxDestination */, false);

		// Check if there are any WalkerBlocker instances in path
		// FIXME: Should be done Path.searchPath()?
		boolean blockerInPath = false;
		for (final Node node: path) {
			final int pathX = node.getX();
			final int pathY = node.getY();

			blockerInPath = !zone.isAreaOccupiable(pathX, pathY);

			if (blockerInPath) {
				break;
			}
		}

		final boolean res = !path.isEmpty() && !blockerInPath;
		if (!res) {
			player.sendPrivateText("There is no easy path to that place.");
		}

		return res;
	}

	/**
	 * Checks if an area can be occupied by an entity.
	 *
	 * @param player
	 * 		Player that is dropping the item.
	 * @param zone
	 * 		Zone where item is dropped.
	 * @param x
	 * 		Horizontal coordinate of target position.
	 * @param y
	 * 		Vertical coordinate of target position.
	 * @return
	 * 		<code>true</code> if the area can be occupied.
	 */
	private boolean isTargetOccupiable(final Player player, final StendhalRPZone zone, final int x, final int y) {
		if (!zone.isAreaOccupiable(x, y)) {
			player.sendPrivateText("There is no space on there.");
			return false;
		}

		return true;
	}
}
