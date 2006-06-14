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

package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.rule.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;

/**
 * Represents the behaviour of a NPC who is able to sell items
 * to a player.
 */
public class SellerBehaviour {
	
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Behaviours.class);

	protected StendhalRPWorld world;
	
	protected Map<String, Integer> priceList;

	protected String chosenItem;

	protected int amount;

	public SellerBehaviour(StendhalRPWorld world) {
		this(world, new HashMap<String, Integer>());
	}

	public SellerBehaviour(StendhalRPWorld world, Map<String, Integer> priceList) {
		this.world = world;
		this.priceList = priceList;
	}

	/**
	 * Returns a set of the names of all items that the NPC sells.
	 * @return the offered items
	 */
	public Set<String> offeredItems() {
		return priceList.keySet();
	}

	/**
	 * Checks whether the NPC sells the specified item.
	 * @param item the name of the item
	 * @return true iff the NPC sells the item
	 */
	public boolean hasItem(String item) {
		return priceList.containsKey(item);
	}

	/**
	 * Returns the price of one unit of a given item.
	 * @param item the name of the item
	 * @return the unit price
	 */
	protected int getUnitPrice(String item) {
		return priceList.get(item);
	}

	/**
	 * Sets the amount that the player wants to buy from the NPC.
	 * @param text a String containing an integer number. If it isn't an
	 *             integer, the amount will be set to 1.
	 */
	public void setAmount(String text) {
		try {
			amount = Integer.parseInt(text);
		} catch (Exception e) {
			amount = 1;
		}
	}

	/**
	 * Returns the price of the desired amount of the chosen item.
	 * @param player The player who considers buying
	 * @return The price; 0 if no item was chosen or if the amount is 0.
	 */
	protected int getCharge(Player player) {
		if (chosenItem == null) {
			return 0;
		} else {
			return amount * getUnitPrice(chosenItem);
		}
	}

	/**
	 * Transacts the sale that has been agreed on earlier via
	 * setChosenItem() and setAmount().
	 * @param seller The NPC who sells
	 * @param player The player who buys
	 * @return true iff the transaction was successful, that is when the
	 *              player was able to equip the item(s).
	 */
	protected boolean transactAgreedSale(SpeakerNPC seller, Player player) {
		EntityManager manager = world.getRuleManager().getEntityManager();

		Item item = manager.getItem(chosenItem);
		if (item == null) {
			logger.error("Trying to sell an unexisting item: " + chosenItem);
			return false;
		}

		// TODO: When the user tries to buy several of a non-stackable
		// item, he is forced to buy only one.
		if (item instanceof StackableItem) {
			((StackableItem) item).setQuantity(amount);
		} else {
			amount = 1;
		}

		item.put("zoneid", player.get("zoneid"));
		IRPZone zone = world.getRPZone(player.getID());
		zone.assignRPObjectID(item);

		if (player.isEquipped("money", getCharge(player))) {
			if (player.equip(item)) {
				player.drop("money", getCharge(player));
				seller.say("Congratulations! Here is your " + chosenItem + "!");
				return true;
			} else {
				seller.say("Sorry, but you cannot equip the " + chosenItem + ".");
				return false;
			}
		} else {
			seller.say("A real pity! You don't have enough money!");
			return false;
		}
	}
}