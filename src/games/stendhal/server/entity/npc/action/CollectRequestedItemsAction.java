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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;

/**
 * handles item lists a player has to bring for a quest
 *
 * @see games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction
 * @author madmetzger
 */
@Dev(category=Category.OTHER, label="Item-")
public final class CollectRequestedItemsAction implements ChatAction {

	private final String itemName;
	private final String questionForMore;
	private final String alreadyBrought;
	private final ChatAction toExecuteOnCompletion;
	private final String questSlot;
	private final ConversationStates stateAfterCompletion;

	/**
	 * create a new CollectRequestedItemsAction
	 *
	 * @param itemName name of the item to process
	 * @param quest the quest to deal with
	 * @param questionForMore How shall the affected NPC ask for more brought items?
	 * @param alreadyBrought What shall the affected NPC say about an already brought item?
	 * @param completionAction action to execute after the complete list was brought
	 * @param stateAfterCompletion state to change to after completion
	 */
	public CollectRequestedItemsAction(String itemName, String quest, String questionForMore, String alreadyBrought, ChatAction completionAction, ConversationStates stateAfterCompletion) {
		this.itemName = checkNotNull(itemName);
		this.questSlot = checkNotNull(quest);
		this.questionForMore = checkNotNull(questionForMore);
		this.alreadyBrought = checkNotNull(alreadyBrought);
		this.toExecuteOnCompletion = checkNotNull(completionAction);
		this.stateAfterCompletion = checkNotNull(stateAfterCompletion);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		ItemCollection missingItems = getMissingItems(player);
		final Integer missingCount = missingItems.get(itemName);

		if ((missingCount != null) && (missingCount > 0)) {
			if (dropItems(player, missingCount)) {
				missingItems = getMissingItems(player);

				if (missingItems.size() > 0) {
					raiser.say(questionForMore);
				} else {
					toExecuteOnCompletion.fire(player, sentence, raiser);
					raiser.setCurrentState(this.stateAfterCompletion);
				}
			} else {
				raiser.say("You don't have " + Grammar.a_noun(itemName) + " with you!");
			}
		} else {
			raiser.say(alreadyBrought);
		}
	}

	/**
	 * Drop specified amount of given item. If player doesn't have enough items,
	 * all carried ones will be dropped and number of missing items is updated.
	 *
	 * @param player    player
	 * @param itemCount count
	 * @return true if something was dropped
	 */
	boolean dropItems(final Player player, int itemCount) {
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

	@Override
	public int hashCode() {
		return 5051 * (itemName.hashCode()
				+ 5059 * (questSlot.hashCode()
				+ 5077 * (questionForMore.hashCode()
				+ 5081 * (alreadyBrought.hashCode()
				+ 5087 * (toExecuteOnCompletion.hashCode()
				+ 5099 * stateAfterCompletion.hashCode())))));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CollectRequestedItemsAction)) {
			return false;
		}
		CollectRequestedItemsAction other = (CollectRequestedItemsAction) obj;
		return itemName.equals(other.itemName)
			&& questSlot.equals(other.questSlot)
			&& questionForMore.equals(other.questionForMore)
			&& alreadyBrought.equals(other.alreadyBrought)
			&& toExecuteOnCompletion.equals(other.toExecuteOnCompletion)
			&& stateAfterCompletion.equals(other.stateAfterCompletion);
	}

	@Override
	public String toString() {
		return "CollectRequestedItemsAction < state on completion: "+stateAfterCompletion.toString()+", execute on completion: "+toExecuteOnCompletion.toString()+">";
	}

}
