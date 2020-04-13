/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.action;

import java.util.Objects;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


/**
 * Action to adjust amount of an item that is looted, produced, harvested, bought, sold, etc.
 */
public class IncreaseItemExchangeAction implements ChatAction {

	private final static Logger logger = Logger.getLogger(IncreaseItemExchangeAction.class);

	private final String exchangeType;
	private final String itemName;
	private final int quantity;


	/**
	 * Creates an action to increase count of an exchanged item by specified amount.
	 *
	 * @param exchangeType
	 * 		String represented the type of transaction. Must be one of: obtain, loot, produce,
	 * 		harvest, mine, sell, or buy.
	 * @param itemName
	 * 		Name of the item.
	 * @param quantity
	 * 		The amount to increase by.
	 */
	public IncreaseItemExchangeAction(final String exchangeType, final String itemName, final int quantity) {
		this.exchangeType = exchangeType.toLowerCase();
		this.itemName = itemName;
		this.quantity = quantity;
	}

	/**
	 * Creates an action to increase count of an exchanged item by 1.
	 *
	 * @param exchangeType
	 * 		String represented the type of transaction. Must be one of: obtain, loot, produce,
	 * 		harvest, mine, sell, or buy.
	 * @param itemName
	 * 		Name of the item.
	 */
	public IncreaseItemExchangeAction(final String exchangeType, final String itemName) {
		this(exchangeType, itemName, 1);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		switch (exchangeType) {
		case "obtain":
			player.incObtainedForItem(itemName, quantity);
			break;
		case "loot":
			player.incLootForItem(itemName, quantity);
			break;
		case "produce":
			player.incProducedForItem(itemName, quantity);
			break;
		case "harvest":
			player.incHarvestedForItem(itemName, quantity);
			break;
		case "mine":
			player.incMinedForItem(itemName, quantity);
			break;
		case "sell":
			player.incSoldForItem(itemName, quantity);
			break;
		case "buy":
			player.incBoughtForItem(itemName, quantity);
			break;
		default:
			logger.error("Unknown exchange type: " + exchangeType);
		}
	}

	@Override
	public String toString() {
		return "IncreaseItemExchangeAction [exchangeType=" + exchangeType + ", itemName=" + itemName + ", quantity="
				+ quantity + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(exchangeType, itemName, quantity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IncreaseItemExchangeAction)) {
			return false;
		}

		final IncreaseItemExchangeAction other = (IncreaseItemExchangeAction) obj;
		return Objects.equals(exchangeType, other.exchangeType) && Objects.equals(itemName, other.itemName)
				&& quantity == other.quantity;
	}
}
