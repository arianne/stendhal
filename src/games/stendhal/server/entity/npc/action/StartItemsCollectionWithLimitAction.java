/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * For quests that use collections with random quantities for each item.
 *
 * @author AntumDeluge
 */
public class StartItemsCollectionWithLimitAction implements ChatAction {
	private final List<String> items;
	private final int limit;

	/** Quest slot name. */
	private final String questSlot;
	/** Index of sub state. */
	private int itemIndex = 1;

	/**
	 * Creates a new StartItemsCollectionWithLimitsAction.
	 *
	 * @param quest
	 * 			Quest slot name
	 * @param items
	 * 			List of items required
	 * @param limit
	 * 			The sum of all items
	 */
	public StartItemsCollectionWithLimitAction(final String quest, final List<String> items, int limit) {
		this.questSlot = checkNotNull(quest);
		this.items = new LinkedList<String>(items);
		this.limit = limit;
	}

	/**
	 * Creates a new StartItemsCollectionWithLimitsAction.
	 *
	 * @param quest
	 * 			Quest slot name
	 * @param index
	 * 			index of sub state
	 * @param items
	 * 			List of items required
	 * @param limit
	 * 			The sum of all items
	 */
	public StartItemsCollectionWithLimitAction(final String quest, final int index, final List<String> items, int limit) {
		this.questSlot = checkNotNull(quest);
		this.items = new LinkedList<String>(items);
		this.limit = limit;
		this.itemIndex = index;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		int[] requestedQuantities = randomVector(items.size(), limit);

		final StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < items.size(); i++) {
			int quantity = requestedQuantities[i];
			if (quantity != 0) {
				sb.append(items.get(i) + "=" + quantity + ";");
			}
		}

		final String result = sb.toString().substring(0, sb.toString().length() - 1);
		player.setQuest(questSlot, itemIndex, result);
	}

	/**
	 * Create an integer array of defined length, and random contents with a
	 * defined total sum.
	 *
	 * @param length length of the requested array
	 * @param sum sum of the array contents
	 * @return array of random integers
	 */
	private int[] randomVector(int length, int sum) {
		int[] values = new int[length];
		values[0] = sum;
		for (int i = 1; i < values.length; i++) {
			values[i] = Rand.randUniform(0, sum);
		}
		Arrays.sort(values);
		int low = 0;
		for (int i = 0; i < values.length; i++) {
			int rnd = values[i] - low;
			low = values[i];
			values[i] = rnd;
		}
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemIndex;
		result = prime * result + items.hashCode();
		result = prime * result + limit;
		result = prime * result + questSlot.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StartItemsCollectionWithLimitAction)) {
			return false;
		}
		StartItemsCollectionWithLimitAction other = (StartItemsCollectionWithLimitAction) obj;
		if (itemIndex != other.itemIndex) {
			return false;
		}
		if (!items.equals(other.items)) {
			return false;
		}
		if (limit != other.limit) {
			return false;
		}
		if (!questSlot.equals(other.questSlot)) {
			return false;
		}
		return true;
	}
}
