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
import games.stendhal.server.util.StringUtils;

/**
 * States the name of the item, with formatting/grammar rules, stored in the quest slot
 *
 * @see games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction
 * @see games.stendhal.server.entity.npc.action.DropRecordedItemAction
 * @see games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition
 *
 */
@Dev(category=Category.ITEMS_OWNED, label="\"...\"")
public class SayRequiredItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(SayRequiredItemAction.class);

	private final String questname;
	private final String message;
	private final int index;

	/**
	 * Creates a new SayRequiredItemAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param index
	 *            index of sub state
	 * @param message
	 *            message with substitution defined for item: [item], [#item], or [the item]
	 */
	@Dev
	public SayRequiredItemAction(final String questname, @Dev(defaultValue="1") final int index, final String message) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.message = checkNotNull(message);
	}
	/**
	 * Creates a new SayRequiredItemAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param message
	 * 		      message with substitution defined for item: [item], [#item], or [the item]
	 */
	public SayRequiredItemAction(final String questname, final String message) {
		this.questname = checkNotNull(questname);
		this.message = checkNotNull(message);
		this.index = -1;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (!player.hasQuest(questname)) {
			logger.error(player.getName() + " does not have quest " + questname);
			return;
		} else {
			String itemname = player.getRequiredItemName(questname, index);
			int amount = player.getRequiredItemQuantity(questname, index);

			Map<String, String> substitutes = new HashMap<String, String>();
			substitutes.put("item", Grammar.quantityplnoun(amount, itemname, "a"));
			substitutes.put("#item", Grammar.quantityplnounWithHash(amount, itemname));
			substitutes.put("the item", "the " + Grammar.plnoun(amount, itemname));

			raiser.say(StringUtils.substitute(message,substitutes));
		}
	}

	@Override
	public String toString() {
		return "SayRequiredItemAction <" + questname +  "\"," + index + ",\"" + message + ">";
	}

	@Override
	public int hashCode() {
		return 5393 * (questname.hashCode() + 5407 * (message.hashCode() + 5413 * index));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayRequiredItemAction)) {
			return false;
		}
		SayRequiredItemAction other = (SayRequiredItemAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname)
			&& message.equals(other.message);
	}

}
