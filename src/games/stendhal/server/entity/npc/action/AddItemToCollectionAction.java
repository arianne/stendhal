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

/**
 * Adds an item to a player's quest slot string
 * 
 * @author AntumDeluge
 */
public class AddItemToCollectionAction implements ChatAction {
	
	private String item;
	private int quantity;
	
	private String QUEST_SLOT;
	
	private int ITEM_INDEX;
	
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
		
		String[] collectionItems = this.QUEST_SLOT.split(";");
		ITEM_INDEX = collectionItems.length;
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
	
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final String result = item + "=" + quantity;
		player.setQuest(QUEST_SLOT, ITEM_INDEX, result);
	}
	
}