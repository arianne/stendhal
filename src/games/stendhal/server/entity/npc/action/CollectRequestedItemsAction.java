/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import java.util.List;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;
/**
 * This {@link ChatAction} handles item lists a player has to bring for a quest
 *  
 * @author madmetzger
 *
 */
public final class CollectRequestedItemsAction implements ChatAction {
	
	private final String questionForMore;
	private final String alreadyBrought;
	private final ChatAction toExecuteOnCompletion;
	private final String questSlot;
	private final ConversationStates stateAfterCompletion;
	/**
	 * create a new {@link CollectRequestedItemsAction}
	 * @param quest the quest to deal with
	 * @param questionForMore How shall the affected NPC ask for more brought items?
	 * @param alreadyBrought What shall the affected NPC say about an already brought item?
	 * @param completionAction action to execute after the complete list was brought
	 * @param stateAfterCompletion state to change to after completion
	 */
	public CollectRequestedItemsAction(String quest, String questionForMore, String alreadyBrought, ChatAction completionAction, ConversationStates stateAfterCompletion) {
		this.questSlot = quest;
		this.questionForMore = questionForMore;
		this.alreadyBrought = alreadyBrought;
		this.toExecuteOnCompletion = completionAction;
		this.stateAfterCompletion = stateAfterCompletion;
	}
	
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
	    final String item = sentence.getTriggerExpression().getNormalized();
	    ItemCollection missingItems = getMissingItems(player);
		final Integer missingCount = missingItems.get(item);

		if ((missingCount != null) && (missingCount > 0)) {
			if (dropItems(player, item, missingCount)) {
				missingItems = getMissingItems(player);

				if (missingItems.size() > 0) {
					raiser.say(questionForMore);
				} else {
					toExecuteOnCompletion.fire(player, sentence, raiser);
					raiser.setCurrentState(this.stateAfterCompletion);
				}
			} else {
				raiser.say("You don't have " + item + " with you!");
			}
		} else {
			raiser.say(alreadyBrought);
		}
	}
	
	/**
	 * Drop specified amount of given item. If player doesn't have enough items,
	 * all carried ones will be dropped and number of missing items is updated.
	 *
	 * @param player
	 * @param itemName
	 * @param itemCount
	 * @return true if something was dropped
	 */
	boolean dropItems(final Player player, final String itemName, int itemCount) {
		boolean result = false;

		 // parse the quest state into a list of still missing items
		final ItemCollection itemsTodo = new ItemCollection();

		itemsTodo.addFromQuestStateString(player.getQuest(questSlot));

		if (player.drop(itemName, itemCount)) {
			if (itemsTodo.removeItem(itemName, itemCount)) {
				result = true;
			}
		} else {
			/*
			 * handle the cases the player has part of the items or all divided
			 * in different slots
			 */
			final List<Item> items = player.getAllEquipped(itemName);
			if (items != null) {
				for (final Item item : items) {
					final int quantity = item.getQuantity();
					final int n = Math.min(itemCount, quantity);

					if (player.drop(itemName, n)) {
						itemCount -= n;

						if (itemsTodo.removeItem(itemName, n)) {
							result = true;
						}
					}

					if (itemCount == 0) {
						result = true;
						break;
					}
				}
			}
		}

		 // update the quest state if some items are handed over
		if (result) {
			player.setQuest(questSlot, itemsTodo.toStringForQuestState());
		}

		return result;
	}
	
	/**
	 * Returns all items that the given player still has to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @return A list of item names
	 */
	ItemCollection getMissingItems(final Player player) {
		final ItemCollection missingItems = new ItemCollection();

		missingItems.addFromQuestStateString(player.getQuest(questSlot));

		return missingItems;
	}
}