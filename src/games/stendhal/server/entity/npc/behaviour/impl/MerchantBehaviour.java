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
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.entity.npc.parser.ExpressionType;
import games.stendhal.server.entity.npc.parser.WordList;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Represents the behaviour of a NPC who is able to either sell items to a
 * player, or buy items from a player.
 */
public abstract class MerchantBehaviour extends TransactionBehaviour {
	private static Logger logger = Logger.getLogger(MerchantBehaviour.class);

	protected Map<String, Integer> priceList;

	public MerchantBehaviour() {
		this(new HashMap<String, Integer>());
	}

	public MerchantBehaviour(final Map<String, Integer> priceList) {
		super(priceList.keySet());

		this.priceList = priceList;

		for (final String itemName : priceList.keySet()) {
			WordList.getInstance().registerName(itemName, ExpressionType.OBJECT);
		}
	}

	/**
	 * Returns a set of the names of all items that the NPC deals with.
	 * 
	 * @return the dealt items
	 */
	public Set<String> dealtItems() {
		return priceList.keySet();
	}

	/**
	 * Checks whether the NPC deals with the specified item.
	 * 
	 * @param item
	 *            the name of the item
	 * @return true iff the NPC deals with the item
	 */
	public boolean hasItem(final String item) {
		return priceList.containsKey(item);
	}

	/**
	 * Returns the price of one unit of a given item.
	 * 
	 * @param item
	 *            the name of the item
	 * @return the unit price
	 */
	public int getUnitPrice(final String item) {
		return priceList.get(item);
	}

	/**
	 * Sets the amount that the player wants to buy from the NPC.
	 * 
	 * @param amount
	 *            amount
	 */
	@Override
	public void setAmount(final int amount) {

		if (amount < 1) {
			this.amount = 1;
			logger.warn("Increasing very low amount of " + amount + " to 1.");
		}
		if (amount > 1000) {
			logger.warn("Decreasing very large amount of " + amount + " to 1.");
			this.amount = 1;
		}
		this.amount = amount;
	}

	/**
	 * Returns the price of the desired amount of the chosen item.
	 * @param player
	 *            The player who considers buying/selling
	 * @param npc
	 * 			  The merchant NPC
	 * 
	 * @return The price; 0 if no item was chosen or if the amount is 0.
	 */
	public int getCharge(final Player player) {
		if (chosenItemName == null) {
			return 0;
		} else {
			return amount * getUnitPrice(getChosenItemName());
		}
	}
}
