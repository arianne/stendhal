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
package games.stendhal.server.maps.quests.logic;

import games.stendhal.common.grammar.Grammar;

/**
 * Container for a collectible item. Holds information about the initial
 * required amount, as well as the current still needed amount, and a message
 * describing the need of the item. The recommended way of building an instance
 * of this type if by using {@link ItemCollector#require()}.
 */
public class ItemCollectorData implements ItemCollectorSetters {

	private String itemName;
	private int requiredAmount = 1;
	private String message;

	private int stillNeededAmount = 1;

	@Override
	public ItemCollectorSetters item(String itemName) {
		this.itemName = itemName;
		return this;
	}

	@Override
	public ItemCollectorSetters pieces(int count) {
		this.requiredAmount = count;
		this.stillNeededAmount = count;
		return this;
	}

	@Override
	public ItemCollectorSetters bySaying(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Subtract the specified amount from the still needed amount.
	 *
	 * @param amount
	 *            how much to subtract from the still needed amount
	 */
	public void subtractAmount(final int amount) {
		stillNeededAmount -= amount;
	}

	/**
	 * Subtract the specified amount from the still needed amount.
	 *
	 * @param amount
	 *            a number indicating how much to subtract from the still needed
	 *            amount
	 */
	public void subtractAmount(final String amount) {
		subtractAmount(Integer.parseInt(amount));
	}

	/**
	 * Makes the still needed amount equal to the initial required amount.
	 */
	public void resetAmount() {
		stillNeededAmount = requiredAmount;
	}

	/**
	 * Gets the number of already collected items.
	 *
	 * @return the difference between the initial required amount and the still
	 *         needed amount
	 */
	public int getAlreadyBrought() {
		return requiredAmount - stillNeededAmount;
	}

	/**
	 * Gets the number of items that are still needed.
	 *
	 * @return the difference between the initial required amount and the amount
	 *         already brought
	 */
	public int getStillNeeded() {
		return stillNeededAmount;
	}

	/**
	 * Gets the number of initially required items.
	 *
	 * @return the number of initially required items
	 */
	public int getRequiredAmount() {
		return requiredAmount;
	}

	/**
	 * Gets the name of the item to collect.
	 *
	 * @return the name of the item to collect
	 */
	public String getName() {
		return itemName;
	}

	/**
	 * Uses the configured message to describe the need of this item.
	 *
	 * @return a message describing the need of this item.
	 * @see #bySaying(String)
	 */
	public String getAnswer() {
		String neededItems = Grammar.quantityplnoun(stillNeededAmount, itemName, "a");
		return String.format(message, neededItems);
	}
}
