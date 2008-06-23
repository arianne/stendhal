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

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * This listener handles all entity movements from a slot to either another slot
 * or the ground.
 * 
 * The source can be: baseitem - object id of the item which should be moved
 * 
 * (optional, only when the source is inside a slot) baseobject - (only when the
 * item is in a slot) object id of the object containing the slot where the item
 * is in baseslot - (only when the item is in a slot) slot name where the item
 * is in (/optional)
 * 
 * 
 * The target can be either an 'equip': type - "equip" targetobject - object id
 * of the container object targetslot - slot name where the item should be moved
 * to
 * 
 * or a 'drop': type - "drop" x - the x-coordinate on the ground y - the
 * y-coordinate on the ground
 */
public class EquipmentAction implements ActionListener {

	static final Logger logger = Logger.getLogger(EquipmentAction.class);

	/** the list of valid container classes. */
	private static final Class< ? >[] validContainerClasses = new Class< ? >[] {
			Player.class, Chest.class, Corpse.class };

	/** List of the valid container classes for easy access. */
	private List<Class< ? >> validContainerClassesList;

	/**
	 * registers "equip" and "drop" action processors.
	 */
	public static void register() {
		EquipmentAction equip = new EquipmentAction();
		CommandCenter.register("equip", equip);
		CommandCenter.register("drop", equip);
	}

	/** constructor. */
	public EquipmentAction() {
		validContainerClassesList = Arrays.asList(validContainerClasses);
	}

	public void onAction(Player player, RPAction action) {

		// HACK: No item transfer in jail (we don't want a jailed player to
		// create a new free character and give it all items.
		if (player.getZone().getName().endsWith("_jail")) {
			player.sendPrivateText("For security reasons, items may not be moved around in jail.");
			return;
		}

		if (action.get(EquipActionConsts.TYPE).equals("equip")) {
			onEquip(player, action);
		} else {
			onDrop(player, action);
		}
	}

	/** callback for the equip action. */
	private void onEquip(Player player, RPAction action) {
		// get source and check it
		logger.debug("Checking source object conditions: " + action);
		SourceObject source = SourceObject.createSourceObject(action, player);
		if (!source.isValid()) {
			logger.debug("Source is not valid");
			return;
		}

		if (!source.checkDistance(player, EquipActionConsts.MAXDISTANCE)) {
			logger.debug("Source is not valid: source too far from player.");
			return;
		}

		if (!source.checkClass(validContainerClassesList)) {
			logger.debug("Source is not valid: Not valid class");
			return;
		}

		logger.debug("Getting entity name");
		// is the entity unbound or bound to the right player?
		Entity entity = source.getEntity();
		String itemName = "entity";
		if (entity.has("name")) {
			itemName = entity.get("name");
		} else if (entity instanceof Item) {
			itemName = "item";
		}

		logger.debug("Checking minimum level");
		// check minimum level
		if (entity.has("min_level")
				&& player.getLevel() < entity.getInt("min_level")) {
			player.sendPrivateText("You are not experienced enough to use this "
					+ itemName);
			return;
		}

		logger.debug("Checking if entity is bound");
		if (entity instanceof Item) {
			Item item = (Item) entity;
			if (item.isBound() && !item.isBoundTo(player)) {
				player.sendPrivateText("This " + itemName
						+ " is a special reward for " + item.getBoundTo()
						+ ". You do not deserve to use it.");
				return;
			}
			
		}

		logger.debug("Checking destination");
		// get destination and check it
		DestinationObject dest = new DestinationObject(action, player);
		if (!dest.isValid()
				|| !dest.checkDistance(player, EquipActionConsts.MAXDISTANCE)
				|| !dest.checkClass(validContainerClassesList)) {
			// destination is not valid
			logger.debug("Destination is not valid");
			return;
		}

		logger.debug("Equip action agreed");

		// looks good
		if (source.moveTo(dest, player)) {
			int amount = 1;
			if (entity instanceof StackableItem) {
				amount = ((StackableItem) entity).getQuantity();
			}
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "equip",
					itemName, source.getSlot(), dest.getSlot(),
					Integer.toString(amount));
	
			player.updateItemAtkDef();
		}
	}

	private void onDrop(Player player, RPAction action) {
		// get source and check it
		SourceObject source = SourceObject.createSourceObject(action, player);
		if (!source.isValid()
				|| !source.checkDistance(player, EquipActionConsts.MAXDISTANCE)
				|| !source.checkClass(validContainerClassesList)) {
			// source is not valid
			return;
		}

		// get destination and check it
		DestinationObject dest = new DestinationObject(action, player);
		if (!dest.isValid() || !dest.checkDistance(player, 5.0)
				|| !dest.checkClass(validContainerClassesList)) {
			logger.warn("destination is invalid. action is: " + action);
			// destination is not valid
			return;
		}

		Entity entity = source.getEntity();
		String itemName = "entity";
		if (entity.has("name")) {
			itemName = entity.get("name");
		} else if (entity instanceof Item) {
			itemName = "item";
		}

		if (source.moveTo(dest, player)) {
			if (entity instanceof Item) {
				Item item = (Item) entity;
				if (item.isBound()) {

					player.sendPrivateText("You put a valuable item on the ground. Please note that it will expire in "
							+ (Item.DEGRADATION_TIMEOUT / 60)
							+ " minutes, as all items do. But in this case there is no way to restore it.");
				}
			}
			
			int amount = source.getQuantity();
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "drop",
					itemName, source.getSlot(), dest.getSlot(),
					Integer.toString(amount));
			player.updateItemAtkDef();
		}
	}

}
