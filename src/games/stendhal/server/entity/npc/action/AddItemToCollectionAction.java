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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Adds an item to a player's quest slot string
 * 
 * @author AntumDeluge
 */
public class AddItemToCollectionAction implements ChatAction {
	
	private String item;
	private int quantity;
	
	private String QUEST_SLOT;
	
	private int ITEM_INDEX = -1;
	
	/**
	 * Creates a new AddItemToCollectionAction
	 * 
	 * @param quest
	 * 			Quest slot name
	 * @param item
	 * 			Item name
	 * @param quantity
	 * 			Item quantity
	 */
	public AddItemToCollectionAction(final String quest, final String item, int quantity) {
		this.QUEST_SLOT = quest;
		this.item = item;
		this.quantity = quantity;
	}
	
	/**
	 * Creates a new StartItemsCollectionWithLimitsAction
	 * 
	 * @param quest
	 * 			Quest slot name
	 * @param index
	 * 			index of sub state
	 * @param item
	 * 			Item name
	 * @param quantity
	 * 			Item quantity
	 */
	public AddItemToCollectionAction(final String quest, final int index, final String item, int quantity) {
		this.QUEST_SLOT = quest;
		this.ITEM_INDEX = index;
		this.item = item;
		this.quantity = quantity;
	}
	
	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		int index = ITEM_INDEX;
		
		String result = item + "=" + quantity;		
		
		String[] itemList = player.getQuest(QUEST_SLOT).split(";");
		
		if (index < 0) {
			index = itemList.length;
			player.setQuest(QUEST_SLOT, index, result);
		} else {
			List<String> itemListArray = new ArrayList<String>();
			
			// add the original elements
			for (int x = 0; x < itemList.length; x++) {
				itemListArray.add(itemList[x]);
			}
			
			// Add the new element
			if (index > itemListArray.size()) {
				itemListArray.add(result);
			} else {
				itemListArray.add(index, result);
			}
			
			itemList = itemListArray.toArray(itemList);
			player.setQuest(QUEST_SLOT, StringUtils.join(itemList, ";"));
		}
	}
	
}