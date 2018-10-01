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

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;
import games.stendhal.server.util.StringUtils;

/**
 * States the name of the items missing from a quest slot with items like item=amount;item2=amount2;item3=amount3
 *
 * @see games.stendhal.server.entity.npc.action.CollectRequestedItemsAction
 */
@Dev(category=Category.ITEMS_OWNED, label="\"...\"")
public class SayRequiredItemsFromCollectionAction implements ChatAction {
	private static Logger logger = Logger.getLogger(SayRequiredItemsFromCollectionAction.class);

	private final String questname;
	private final String message;
	private final int index;
	private final boolean commaString;

	/**
	 * Creates a new SayRequiredItemssFromCollectionAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param message
	 *            message with substitution [items] for the list of items
	 */
	public SayRequiredItemsFromCollectionAction(final String questname, final String message) {
		this.questname = checkNotNull(questname);
		this.index = 0;
		this.message = checkNotNull(message);
		this.commaString = false;
	}

	/**
	 * Creates a new SayRequiredItemssFromCollectionAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param index index of sub state
	 * @param message
	 *            message with substitution [items] for the list of items
	 */
	public SayRequiredItemsFromCollectionAction(final String questname, final int index, final String message) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.message = checkNotNull(message);
		this.commaString = false;
	}

	/*
	 * Hack to get items from quest state index using comma-separated string.
	 */
	public SayRequiredItemsFromCollectionAction(final String questname, final String message, final boolean commaString) {
		this.questname = questname;
		this.index = 0;
		this.message = checkNotNull(message);
		this.commaString = commaString;
	}

	/*
	 * Hack to get items from quest state index using comma-separated string.
	 */
	public SayRequiredItemsFromCollectionAction(final String questname, final int index, final String message, final boolean commaString) {
		this.questname = questname;
		this.index = index;
		this.message = checkNotNull(message);
		this.commaString = commaString;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (!player.hasQuest(questname)) {
			logger.error(player.getName() + " does not have quest " + questname);
			return;
		} else {
			Map<String, String> substitutes = new HashMap<String, String>();
			substitutes.put("items", Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash()));

			raiser.say(StringUtils.substitute(message,substitutes));
		}
	}

	/**
	 * Returns all items that the given player still has to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @return A list of item names
	 */
	private ItemCollection getMissingItems(final Player player) {
		final ItemCollection missingItems = new ItemCollection();

		// Hack to get items from quest state index using comma-separated string.
		if (!commaString) {
			missingItems.addFromQuestStateString(player.getQuest(questname), index);
		} else {
			missingItems.addFromString(player.getQuest(questname, index));
		}

		return missingItems;
	}

	@Override
	public String toString() {
		return "SayRequiredItemsFromCollectionAction <" + questname +  "\"," + message + ">";
	}

	@Override
	public int hashCode() {
		return 5393 * (questname.hashCode() + 5407 * (message.hashCode() + 5413 * index));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayRequiredItemsFromCollectionAction)) {
			return false;
		}
		SayRequiredItemsFromCollectionAction other = (SayRequiredItemsFromCollectionAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname)
			&& message.equals(other.message);
	}

}
