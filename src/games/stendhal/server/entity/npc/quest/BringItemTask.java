/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.DropFirstOwnedItemAction;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import marauroa.common.Pair;

/**
 * request a quantity of a single item from a player. Alternative items are supported
 *
 * @author hendrik
 */
public class BringItemTask extends QuestTaskBuilder {
	private Pair<String, Integer> requestItem;
	private List<Pair<String, Integer>> alternativeItems = new LinkedList<>();

	// hide constructor
	BringItemTask() {
		super();
	}

	/**
	 * request an item from the player
	 *
	 * @param quantity quantity of item
	 * @param name name of item
	 * @return BringItemTask
	 */
	public BringItemTask requestItem(int quantity, String name) {
		requestItem = new Pair<String, Integer>(name, quantity);
		return this;
	}

	/**
	 * accept an alternative item from the player (e. g. pauldroned leather cuirass).
	 *
	 * @param quantity quantity of item
	 * @param name name of item
	 * @return BringItemTask
	 */
	public BringItemTask alternativeItem(int quantity, String name) {
		alternativeItems.add(new Pair<String, Integer>(name, quantity));
		return this;
	}

	@Override
	void simulate(QuestSimulator simulator) {
		simulator.info("Player obtained " + requestItem.second() + " " + requestItem.first() + ".");
		simulator.info("");
	}

	@Override
	ChatAction buildStartQuestAction(String questSlot) {
		return null;
	}

	@Override
	ChatCondition buildQuestCompletedCondition(String questSlot) {
		List<ChatCondition> conditions = new LinkedList<>();
		conditions.add(new PlayerHasItemWithHimCondition(requestItem.first(), requestItem.second()));
		for (Pair<String, Integer> item : alternativeItems) {
			conditions.add(new PlayerHasItemWithHimCondition(item.first(), item.second()));
		}
		return new OrCondition(conditions);
	}

	@Override
	ChatAction buildQuestCompleteAction(String questSlot) {
		LinkedList<Pair<String, Integer>> temp = new LinkedList<>(alternativeItems);
		temp.add(0, requestItem);
		return new DropFirstOwnedItemAction(temp);
	}

}
