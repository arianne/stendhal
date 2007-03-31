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

import games.stendhal.server.entity.player.Player;
import games.stendhal.common.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the behaviour of a NPC who is able to either sell items
 * to a player, or buy items from a player.
 */
public abstract class MerchantBehaviour extends Behaviour {
	
	protected Map<String, Integer> priceList;

	protected String chosenItem;

	private int amount;

	public MerchantBehaviour() {
		this(new HashMap<String, Integer>());
	}

	public MerchantBehaviour(Map<String, Integer> priceList) {
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
		setAmount(MathHelper.parseInt_default(text, 1));
	}

	/**
	 * Sets the amount that the player wants to buy from the NPC.
	 *
	 * @param amount amount
	 */
	public void setAmount(int amount) {
		if (amount < 1) {
			amount = 1;
		}
		if (amount > 1000) {
			amount = 1;
		}
		this.amount = amount;
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

	public int getAmount() {
		return amount;
	}
}