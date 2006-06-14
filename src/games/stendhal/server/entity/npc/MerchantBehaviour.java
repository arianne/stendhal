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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the behaviour of a NPC who is able to either sell items
 * to a player, or buy items from a player.
 */
public abstract class MerchantBehaviour {
	
	protected StendhalRPWorld world;
	
	protected Map<String, Integer> priceList;

	protected String chosenItem;

	protected int amount;

	public MerchantBehaviour(StendhalRPWorld world) {
		this(world, new HashMap<String, Integer>());
	}

	public MerchantBehaviour(StendhalRPWorld world, Map<String, Integer> priceList) {
		this.world = world;
		this.priceList = priceList;
	}

	/**
	 * Returns a set of the names of all items that the NPC deals with.
	 * @return the dealt items
	 */
	public Set<String> dealtItems() {
		return priceList.keySet();
	}

	/**
	 * Checks whether the NPC deals with the specified item.
	 * @param item the name of the item
	 * @return true iff the NPC deals with the item
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
	 * @param player The player who considers buying/selling
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
	 * Transacts the deal that has been agreed on earlier via
	 * setChosenItem() and setAmount().
	 * @param seller The NPC who sells/buys
	 * @param player The player who buys/sells
	 * @return true iff the transaction was successful.
	 */
	protected abstract boolean transactAgreedDeal(SpeakerNPC seller, Player player);
}