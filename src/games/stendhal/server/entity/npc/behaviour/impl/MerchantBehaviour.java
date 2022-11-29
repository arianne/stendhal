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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
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

	// merchant only deals with these items if condition is met
	private Map<String, ChatCondition> conditions;

	private SpeakerNPC merchant;


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
	 * @return
	 *     The dealt items.
	 */
	public Set<String> dealtItems() {
		if (conditions == null || conditions.isEmpty()) {
			return priceCalculator.dealtItems();
		}

		Player player = null;
		if (merchant != null) {
			final RPEntity attending = merchant.getAttending();
			if (attending instanceof Player) {
				player = (Player) attending;
			}
		}

		final Set<String> items = new HashSet<String>();
		for (final String itemName : priceCalculator.dealtItems()) {
			if (!conditions.containsKey(itemName)) {
				items.add(itemName);
			} else if (player != null) {
				if (conditions.get(itemName).fire(player, null, null)) {
					items.add(itemName);
				}
			}
		}

		return items;
	}

	/**
	 * Checks whether the NPC deals with the specified item.
	 *
	 * @param item
	 *     The name of the item.
	 * @return
	 *     <code>true</code> if the NPC deals with the item.
	 */
	public boolean hasItem(final String item) {
		return dealtItems().contains(item);
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

	/**
	 * Adds a list of items that are dealt only if a condition is met.
	 *
	 * @param merchant
	 *     SpeakerNPC that is the merchant.
	 * @param conditions
	 *     List of conditions to check item availability against.
	 */
	public void addConditions(final SpeakerNPC merchant, final Map<String, ChatCondition> conditions) {
		this.merchant = merchant;
		this.conditions = conditions;
	}

	@Override
	public Set<String> getItemNames() {
		return dealtItems();
	}
}
