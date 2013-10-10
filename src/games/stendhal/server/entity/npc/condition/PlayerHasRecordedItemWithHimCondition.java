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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Does the player carry the specified item?
 *
 * @see games.stendhal.server.entity.npc.action.SayRequiredItemAction
 * @see games.stendhal.server.entity.npc.action.DropRecordedItemAction
 * @see games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction
 *
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasRecordedItemWithHimCondition implements ChatCondition {

	private final String questName;
	private final int index;

	/**
	 * Creates a new PlayerHasRecordedItemWithHimCondition.
	 *
	 * @param questName
	 *            name of quest
	 */
	public PlayerHasRecordedItemWithHimCondition(final String questName) {
		this.questName = questName;
		this.index = -1;
	}

	/**
	 * Creates a new PlayerHasRecordedItemWithHimCondition.
	 *
	 * @param questName
	 *            name of quest
	 * @param index
	 *            index in the quest slot
	 */
	@Dev
	public PlayerHasRecordedItemWithHimCondition(final String questName, final int index) {
		this.questName = questName;
		this.index = index;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		String itemName=player.getRequiredItemName(questName,index);
		int amount = player.getRequiredItemQuantity(questName,index);
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has recorded item from questslot <" + questName + ">";
	}

	@Override
	public int hashCode() {
		return 43943 * questName.hashCode() + 43951 * index;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasRecordedItemWithHimCondition)) {
			return false;
		}
		PlayerHasRecordedItemWithHimCondition other = (PlayerHasRecordedItemWithHimCondition) obj;
		return (index == other.index)
			&& questName.equals(other.questName);
	}
}
