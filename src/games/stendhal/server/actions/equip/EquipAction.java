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

import java.util.Arrays;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.constants.Actions;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.Slots;
import marauroa.common.game.RPAction;

public class EquipAction extends EquipmentAction {

	/**
	 * registers "equip" action processor.
	 */
	public static void register() {
		CommandCenter.register("equip", new EquipAction());
	}
	@Override
	protected void execute(final Player player, final RPAction action, final SourceObject source) {
		// get source and check it

		logger.debug("Getting entity name");
		// is the entity unbound or bound to the right player?
		final Entity entity = source.getEntity();
		final String itemName = source.getEntityName();

		logger.debug("Checking if entity is bound");
		if (entity instanceof Item) {
			final Item item = (Item) entity;
			if (item.isBound() && !player.isBoundTo(item)) {
				player.sendPrivateText("This " + itemName
						+ " is a special reward for " + item.getBoundTo()
						+ ". You do not deserve to use it.");
				return;
			}

		}

		final String targetPath = action.get(Actions.TARGET_PATH);
		String targetSlot = null;
		if (targetPath != null) {
			targetSlot = targetPath.substring(targetPath.indexOf("\t") + 1, targetPath.indexOf("]"));
		}

		// try to move money to pouch by default
		if (action.has(EquipActionConsts.CLICKED) && targetSlot != null && !targetSlot.equals("pouch")
				&& source.getEntityName().equals("money")) {
			// check if money can be moved to pouch
			// XXX: this check should be changed if we switch to containers
			if (player.getFeature("pouch") != null && player.hasSlot("pouch")) {
				final boolean moneyInBag = player.isEquippedItemInSlot("bag", "money");
				final boolean moneyInPouch = player.isEquippedItemInSlot("pouch", "money");
				// stack on pouch
				if (moneyInPouch || (!moneyInPouch && !moneyInBag)) {
					action.put(EquipActionConsts.TARGET_SLOT, "pouch");
					if (action.has(Actions.TARGET_PATH)) {
						action.put(Actions.TARGET_PATH,
								Arrays.asList(player.get("id"), "pouch"));
					}
				}
			}
		}

		logger.debug("Checking destination");
		// get destination and check it
		final DestinationObject dest = new DestinationObject(action, player);
		if (dest.isInvalidMoveable(player, EquipActionConsts.MAXDISTANCE, validContainerClassesList)) {
			// destination is not valid
			logger.debug("Destination is not valid");
			return;
		}

		logger.debug("Equip action agreed");

		// looks good
		if (source.moveTo(dest, player)) {
			int amount = source.getQuantity();

			// Warn about min level
			if (player.equals(dest.parent)
					&& Slots.CARRYING.getNames().contains(dest.slot)
					&& !"bag".equals(dest.slot)) {
				if(entity instanceof Item) {
					int minLevel = ((Item) entity).getMinLevel();
					if (minLevel > player.getLevel()) {
						player.sendPrivateText("You are not experienced enough to use this item to full benefit. You are probably better off by using an item appropriate for your level instead.");
					}
				}
			}

			// players sometimes accidentally drop items into corpses, so inform about all drops into a corpse
			// which aren't just a movement from one corpse to another.
			// we could of course specifically preclude dropping into corpses, but that is undesirable.
			if (dest.isContainerCorpse() && !source.isContainerCorpse()) {
				player.sendPrivateText("For your information, you just dropped "
						+ Grammar.quantityplnounWithMarker(amount,entity.getTitle(), '§')
						+ " into a corpse next to you.");
			}

			if(source.isLootingRewardable()) {
				if(entity instanceof Item) {
					((Item) entity).setFromCorpse(false);
				}
				player.incLootForItem(entity.getTitle(), amount);
			}
			if (entity instanceof Item) {
				((Item) entity).autobind(player.getName());
			}

			new GameEvent(player.getName(), "equip", itemName, source.getSlot(), dest.getSlot(), Integer.toString(amount)).raise();

			player.updateItemAtkDef();
		}
	}

}
