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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.StringUtils;

/**
 * Start recording random item collection request.
 * For a quest to use this it needs the list of items with their quantities as a Map<String, Integer>
 *
 * @see games.stendhal.server.entity.npc.action.SayRequiredItemAction
 * @see games.stendhal.server.entity.npc.action.DropRecordedItemAction
 * @see games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition
 */
@Dev(category=Category.ITEMS_OWNED, label="State")
public class StartRecordingRandomItemCollectionAction implements ChatAction {

	private final String questname;
	private final Map<String, Integer> items;
	private final int index;
	private final String message;

	/**
	 * Creates a new StartRecordingRandomItemCollectionAction.
	 *
	 * @param questname
	 *            name of quest-slot to change
	 * @param items
	 *            List of items to select from, with quantity
	 * @param message
	 *            Message which NPC asks for items with. We add the item name and quantity to end of message.
	 */
	public StartRecordingRandomItemCollectionAction(final String questname, final Map<String, Integer> items, final String message) {
		this.questname = checkNotNull(questname);
		this.index = -1;
		this.items = ImmutableMap.copyOf(items);
		this.message = checkNotNull(message);
	}

	/**
	 * Creates a new StartRecordingRandomItemCollectionAction.
	 *
	 * @param questname
	 *            name of quest-slot to change
	 * @param index
	 *            index of sub state
	 * @param items
	 *            List of items name and quantity to select
	 * @param message
	 *            Message which NPC asks for items with. We add the item name and quantity to end of message.
	 */
	@Dev
	public StartRecordingRandomItemCollectionAction(final String questname, @Dev(defaultValue="1") final int index, final Map<String, Integer>
    items, final String message) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.items = ImmutableMap.copyOf(items);
		this.message = checkNotNull(message);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final String itemname = Rand.rand(items.keySet());
		final int quantity = items.get(itemname);

		Map<String, String> substitutes = new HashMap<String, String>();
		substitutes.put("item", Grammar.quantityplnoun(quantity, itemname, "a"));
		substitutes.put("#item", Grammar.quantityplnounWithHash(quantity, itemname));
		substitutes.put("the item", "the " + Grammar.plnoun(quantity, itemname));


		raiser.say(StringUtils.substitute(message,substitutes));
		if (index > -1) {
			player.setQuest(questname, index, itemname + "=" + quantity);
		} else {
			player.setQuest(questname, itemname + "=" + quantity);
		}
	}

	@Override
	public String toString() {
		return "StartRecordingRandomItemCollection<" + items.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 5623 * (questname.hashCode() + 5639 * (index + 5641 * (items.hashCode() + 5647 * message.hashCode())));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof StartRecordingRandomItemCollectionAction)) {
			return false;
		}
		StartRecordingRandomItemCollectionAction other = (StartRecordingRandomItemCollectionAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname)
			&& items.equals(other.items)
			&& message.equals(other.message);
	}
}
