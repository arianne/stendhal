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
package games.stendhal.server.core.rp.achievement.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Checks if a player has sold a number of items.
 */
public class SoldNumberOfCondition implements ChatCondition {

	final Map<String, Integer> itemList;


	/**
	 * Constructor to check a single item.
	 *
	 * @param itemName
	 * 		Name of the item.
	 * @param count
	 * 		Minimum required amount.
	 */
	public SoldNumberOfCondition(final String itemName, final int count) {
		itemList = new HashMap<>();
		itemList.put(itemName, count);
	}

	/**
	 * Constructor to check multiple items.
	 *
	 * @param count
	 * 		Minimum required amount.
	 * @param itemName
	 * 		Names of the items.
	 */
	public SoldNumberOfCondition(final int count, final String... itemName) {
		itemList = new HashMap<>();
		for (final String name: itemName) {
			itemList.put(name, count);
		}
	}

	/**
	 * Constructor to check multiple items.
	 *
	 * @param count
	 * 		Minimum required amount.
	 * @param items
	 * 		List of item names.
	 */
	public SoldNumberOfCondition(final int count, final List<String> items) {
		itemList = new HashMap<>();
		for (final String name: items) {
			itemList.put(name, count);
		}
	}

	/**
	 * Constructor to check multiple items of different amounts.
	 *
	 * @param items
	 * 		List of item & the minimum required amount for each.
	 */
	public SoldNumberOfCondition(final Map<String, Integer> items) {
		itemList = items;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for (final String item: itemList.keySet()) {
			final int required = itemList.get(item);
			final int actual = player.getQuantityOfSoldItems(item);

			if (actual < required) {
				return false;
			}
		}

		return true;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SoldNumberOf [");

		final int itemCount = itemList.size();
		int idx = 0;
		for (final String item: itemList.keySet()) {
			sb.append(item + "=" + itemList.get(item));
			if (idx < itemCount - 1) {
				sb.append(",");
			}
			idx++;
		}

		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 47 * itemList.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SoldNumberOfCondition)) {
			return false;
		}

		final SoldNumberOfCondition other = (SoldNumberOfCondition) obj;
		return itemList.equals(other.itemList);
	}
}
