/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.entity.npc.behaviour.impl.prices.FixedPricePriceCalculationStrategy;
import games.stendhal.server.entity.npc.behaviour.impl.prices.PriceCalculationStrategy;
import games.stendhal.server.entity.player.Player;

/**
 * Represents the behaviour of a NPC who is able to either sell items to a
 * player, or buy items from a player.
 */
public abstract class MerchantBehaviour extends TransactionBehaviour {

//	protected Map<String, Integer> priceList;

	protected PriceCalculationStrategy priceCalculator;

	public MerchantBehaviour() {
		this(new HashMap<String, Integer>());
	}

	public MerchantBehaviour(final Map<String, Integer> priceList) {
		super(priceList.keySet());
		priceCalculator = new FixedPricePriceCalculationStrategy(priceList);
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
		return priceCalculator.dealtItems();
	}

	/**
	 * Checks whether the NPC deals with the specified item.
	 *
	 * @param item
	 *            the name of the item
	 * @return true iff the NPC deals with the item
	 */
	public boolean hasItem(final String item) {
		return priceCalculator.hasItem(item);
	}

	/**
	 * Returns the price of one unit of a given item.
	 *
	 * @param item
	 *            the name of the item
	 * @return the unit price
	 */
	public int getUnitPrice(final String item) {
		return priceCalculator.calculatePrice(item, null);
	}

	/**
	 * Returns the price of the desired amount of the chosen item.
	 * @param res
	 *
	 * @param player
	 *            The player who considers buying/selling
	 *
	 * @return The price; 0 if no item was chosen or if the amount is 0.
	 */
	public int getCharge(ItemParserResult res, final Player player) {
		if (res.getChosenItemName() == null) {
			return 0;
		} else {
			return res.getAmount() * getUnitPrice(res.getChosenItemName());
		}
	}
}
