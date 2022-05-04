/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

/**
 * Drops the specified item.
 *
 * @see games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition
 */
@Dev(category=Category.ITEMS_OWNED, label="Item-")
public class DropFirstOwnedItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropFirstOwnedItemAction.class);
	private final List<Pair<String, Integer>> itemList;

	/**
	 * Creates a new DropFirstOwnedItemAction.
	 *
	 * @param itemList List of pairs of items and quantity
	 */
	@Dev
	public DropFirstOwnedItemAction(List<Pair<String, Integer>> itemList) {
		this.itemList = ImmutableList.copyOf(itemList);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		for (Pair<String, Integer> item : itemList) {
			final boolean res = player.drop(item.first(), item.second());
			if (res) {
				player.notifyWorldAboutChanges();
				return;
			}
		}
		logger.error("Player does not own any of the required items " + itemList, new Throwable());
	}

	@Override
	public String toString() {
		return "drop first item <" + itemList + ">";
	}

	@Override
	public int hashCode() {
		return itemList.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DropFirstOwnedItemAction)) {
			return false;
		}
		DropFirstOwnedItemAction other = (DropFirstOwnedItemAction) obj;
		return itemList.equals(other.itemList);
	}

	public static ChatAction dropFirstOwnedItem(List<Pair<String, Integer>> itemList) {
		return new DropFirstOwnedItemAction(itemList);
	}
}
