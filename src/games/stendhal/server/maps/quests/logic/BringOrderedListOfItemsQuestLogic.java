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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.IQuest;

/**
 * Basic behavior for quests which are based on bringing a list of items to an
 * NPC, in a specific order. The NPC keeps track of the items already brought to
 * him.
 */
public class BringOrderedListOfItemsQuestLogic {

	private ItemCollector itemCollector;

	private IQuest quest;

	/**
	 * Gets a sentence to be said by the NPC, enumerating the items that still
	 * need to be brought. The item names are not prefixed by a hash. For a
	 * variant where they are, see {@link #itemsStillNeededWithHash(Player)}.
	 *
	 * @param player
	 *            the player which needs to bring items
	 * @return an enumeration of still needed items
	 * @see #itemsStillNeededWithHash(Player)
	 */
	public String itemsStillNeeded(final Player player) {
		return itemsStillNeeded(player, false);
	}

	/**
	 * Gets a sentence to be said by the NPC, enumerating the items that still
	 * need to be brought. The item names are prefixed by a hash. For a variant
	 * where they aren't, see {@link #itemsStillNeeded(Player)}.
	 *
	 * @param player
	 *            the player which needs to bring items
	 * @return an enumeration of still needed items
	 * @see #itemsStillNeeded(Player)
	 */
	public String itemsStillNeededWithHash(final Player player) {
		return itemsStillNeeded(player, true);
	}

	private String itemsStillNeeded(final Player player, boolean withHash) {
		List<String> neededItemsWithAmounts = neededItemsWithAmounts(player, withHash);

		// TODO try Grammar.enumerateCollection
		StringBuilder all = new StringBuilder();
		for (int i = 0; i < neededItemsWithAmounts.size(); i++) {
			if (i != 0 && i == neededItemsWithAmounts.size() - 1) {
				all.append(" and ");
			}
			all.append(neededItemsWithAmounts.get(i));
			if (i < neededItemsWithAmounts.size() - 2) {
				all.append(", ");
			}
		}
		return all.toString();
	}

	/**
	 * Gets a list of items that still need to be brought. Each item is prefixed
	 * by the quantity still needed. An example list element: "5 iron bars". For
	 * a sentence that groups the list items, see
	 * {@link #itemsStillNeeded(Player)}.
	 *
	 * @param player
	 *            the player which needs to bring items
	 * @return a list of still needed items, along with their respective
	 *         quantities
	 * @see #itemsStillNeeded(Player)
	 */
	public List<String> neededItemsWithAmounts(final Player player) {
		return neededItemsWithAmounts(player, false);
	}

	private List<String> neededItemsWithAmounts(final Player player, boolean withHash) {
		int[] broughtItems = broughtItems(player);
		List<String> neededItemsWithAmounts = new LinkedList<>();
		for (int i = 0; i < itemCollector.requiredItems().size(); i++) {
			ItemCollectorData item = itemCollector.requiredItems().get(i);
			int required = item.getRequiredAmount();
			int brought = broughtItems[i];
			int neededAmount = required - brought;
			if (neededAmount > 0) {
				neededItemsWithAmounts.add(neededItem(neededAmount, item.getName(), withHash));
			}
		}

		return neededItemsWithAmounts;
	}

	private int[] broughtItems(final Player player) {
		int[] brought = new int[itemCollector.requiredItems().size()];
		String questStatus = player.getQuest(quest.getSlotName());
		if (questStatus != null) {
			String[] broughtTokens = questStatus.split(";");
			for (int i = 1; i < broughtTokens.length; i++) {
				brought[i - 1] = Integer.parseInt(broughtTokens[i]);
			}
		} else {
			Arrays.fill(brought, 0);
		}
		return brought;
	}

	private String neededItem(int neededAmount, String itemName, boolean withHash) {
		if (!withHash) {
			return Grammar.quantityplnoun(neededAmount, itemName, "a");
		} else {
			return Grammar.quantityplnounWithHash(neededAmount, itemName);
		}
	}

	/**
	 * Give items to the NPC, in the required order.
	 *
	 * @param player
	 *            the player which needs to bring items
	 * @param eventRaiser
	 *            the NPC that should say what's the next item to bring
	 * @return {@code true} if there are still items to bring after this, {@code
	 *         false} otherwise
	 */
	public boolean proceedItems(final Player player, final EventRaiser eventRaiser) {
		String questStatus = player.getQuest(quest.getSlotName());
		final String[] tokens = questStatus.split(";");

		int idx1 = 1;
		for (ItemCollectorData itemdata : itemCollector.requiredItems()) {
			itemdata.resetAmount();
			itemdata.subtractAmount(tokens[idx1]);
			idx1++;
		}

		boolean missingSomething = false;

		int size = itemCollector.requiredItems().size();
		for (int idx = 0; !missingSomething && idx < size; idx++) {
			ItemCollectorData itemData = itemCollector.requiredItems().get(idx);
			missingSomething = proceedItem(player, eventRaiser, itemData);
		}

		return missingSomething;
	}

	private boolean proceedItem(final Player player, final EventRaiser engine, final ItemCollectorData itemData) {
		if (itemData.getStillNeeded() > 0) {

			if (player.isEquipped(itemData.getName(), itemData.getStillNeeded())) {
				player.drop(itemData.getName(), itemData.getStillNeeded());
				itemData.subtractAmount(itemData.getStillNeeded());
			} else {
				final int amount = player.getNumberOfEquipped(itemData.getName());
				if (amount > 0) {
					player.drop(itemData.getName(), amount);
					itemData.subtractAmount(amount);
				}

				engine.say(itemData.getAnswer());
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the status of the player's quest with the quantities of already
	 * brought items.
	 *
	 * @param player
	 *            the player for which to update the quest status
	 */
	public void updateQuantitiesInQuestStatus(final Player player) {
		StringBuilder sb = new StringBuilder(30);
		sb.append("start");
		for (ItemCollectorData id : itemCollector.requiredItems()) {
			sb.append(";");
			sb.append(id.getAlreadyBrought());
		}
		player.setQuest(quest.getSlotName(), sb.toString());
	}

	/**
	 * Sets the item collector to be used.
	 *
	 * @param itemCollector
	 *            an {@link ItemCollector}
	 */
	public void setItemCollector(ItemCollector itemCollector) {
		this.itemCollector = itemCollector;
	}

	/**
	 * Sets the quest on which to apply the logic.
	 *
	 * @param quest
	 *            a quest
	 */
	public void setQuest(IQuest quest) {
		this.quest = quest;
	}
}
