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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;
import games.stendhal.server.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * States the name of the items missing from a quest slot with items like item=amount;item2=amount2;item3=amount3
 *
 * @see games.stendhal.server.entity.npc.action.CollectRequestedItemsAction
 */
@Dev(category=Category.ITEMS_OWNED, label="\"...\"")
public class SayRequiredItemsFromCollectionAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropRecordedItemAction.class);

	private final String questname;
	private final String message;
	private final int slotposition;

	/**
	 * Creates a new SayRequiredItemssFromCollectionAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param message
	 *            message with substitution [items] for the list of items
	 */
	public SayRequiredItemsFromCollectionAction(final String questname, final String message) {
		this.questname = questname;
		this.message = message;
		this.slotposition = 0;
	}
	
	public SayRequiredItemsFromCollectionAction(final String questname, final int position, final String message) {
		this.questname = questname;
		this.message = message;
		this.slotposition = position;
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

		missingItems.addFromQuestStateString(player.getQuest(questname), slotposition);

		return missingItems;
	}

	@Override
	public String toString() {
		return "SayRequiredItemsFromCollectionAction <" + questname +  "\"," + message + ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayRequiredItemsFromCollectionAction.class);
	}

}
