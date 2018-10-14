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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

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
	private String questname;
	private int index = -1;

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
		this.questname = checkNotNull(quest);
		this.item = checkNotNull(item);
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
		this.questname = checkNotNull(quest);
		this.index = index;
		this.item = checkNotNull(item);
		this.quantity = quantity;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		String result = item + "=" + quantity;
		String[] itemList = player.getQuest(questname).split(";");

		if (index < 0) {
			index = itemList.length;
			player.setQuest(questname, index, result);
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
			player.setQuest(questname, Joiner.on(";").join(itemList));
		}
	}

	@Override
	public int hashCode() {
		return 5009 * (item.hashCode() + 5011 * (quantity + 5021 * (questname.hashCode() + 5023 * index)));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AddItemToCollectionAction)) {
			return false;
		}
		AddItemToCollectionAction other = (AddItemToCollectionAction) obj;
		return (quantity == other.quantity)
			&& (index == other.index)
			&& questname.equals(other.questname)
			&& item.equals(other.item);
	}
}
