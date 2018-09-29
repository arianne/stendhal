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

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
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

		if (!isTargetOccupiable(action)) {
			player.sendPrivateText("There is no space on there.");
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
	 * Checks if an area can be occupied by an entity.
	 *
	 * @param action
	 * 		RPAction that contains zone ID & coordinates for target.
	 * @return
	 * 		<code>true</code> if the area can be occupied.
	 */
	private boolean isTargetOccupiable(final RPAction action) {
		if (action.has("zoneid") && action.has(EquipActionConsts.GROUND_X) && action.has(EquipActionConsts.GROUND_Y)) {
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(action.get("zoneid"));
			final int x = action.getInt(EquipActionConsts.GROUND_X);
			final int y = action.getInt(EquipActionConsts.GROUND_Y);

			if (!zone.isAreaOccupiable(x, y)) {
				return false;
			}
		}

		return true;
	}
}
