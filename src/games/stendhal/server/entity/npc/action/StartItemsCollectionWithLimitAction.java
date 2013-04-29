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

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * For quests that use collections with random quantities for each item
 * 
 * @author AntumDeluge
 */
public class StartItemsCollectionWithLimitAction implements ChatAction {
	
	private List<String> items;
	private int limit;
	
	private String QUEST_SLOT;
	
	private int ITEM_INDEX = 1;
	
	/**
	 * Creates a new StartItemsCollectionWithLimitsAction
	 * 
	 * @param quest
	 * 			Quest slot name
	 * @param items
	 * 			List of items required
	 * @param limit
	 * 			The sum of all items
	 */
	public StartItemsCollectionWithLimitAction(final String quest, final List<String> items, int limit) {
		this.QUEST_SLOT = quest;
		this.items = items;
		this.limit = limit;
	}
	
	/**
	 * Creates a new StartItemsCollectionWithLimitsAction
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
		this.QUEST_SLOT = quest;
		this.items = items;
		this.limit = limit;
		this.ITEM_INDEX = index;
	}
	
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		List<Integer> requestedQuantities = setRequestedQuantities(this.limit);
		
		final StringBuilder sb = new StringBuilder("");
		
		for (int i = 0; i < this.items.size(); i++) {
			sb.append(this.items.get(i) + "=" + requestedQuantities.get(i) + ";");
		}
		final String result = sb.toString().substring(0, sb.toString().length() - 1);
		player.setQuest(QUEST_SLOT, ITEM_INDEX, result);
	}
	
	/**
	 * 
	 * @param limit
	 * 			Sum of all items
	 * @return
	 * 			List of item quantities
	 */
	
	List<Integer> setRequestedQuantities(int limit) {
		List<Integer> quantities = new ArrayList<Integer>();
		int itemsRemaining = limit;
		int askedAmount;
		
		int i;
		for (i = this.items.size(); i > 0; i--) {
			// Generate a random number for each item type but leave at least 1 for each remaining type
			askedAmount = Rand.randUniform(1, itemsRemaining - (i - 1));
			
			// Add the requested amount for each item to a list
			quantities.add(askedAmount);
			
			itemsRemaining -= askedAmount;
		}
		
		// Re-order quantities to make more random
		long seed = System.nanoTime();
		Collections.shuffle(quantities, new Random(seed));
		
		return quantities;
	}
}