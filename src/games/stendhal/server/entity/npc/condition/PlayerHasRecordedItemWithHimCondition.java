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

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player carry the specified item?
 * 
 * @see games.stendhal.server.entity.npc.action.SayRequiredItemAction
 * @see games.stendhal.server.entity.npc.action.DropRecordedItemAction
 * @see games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction
 * 
 */
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
	public PlayerHasRecordedItemWithHimCondition(final String questName, final int index) {
		this.questName = questName;
		this.index = index;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final String questSubString = player.getQuest(questName, index);
		final String[] elements = questSubString.split("=");
		String itemName=elements[0];
		int amount = 1;
		if(elements.length > 1) {
			amount=MathHelper.parseIntDefault(elements[1], 1);
		} 
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has recorded item from questslot <" + questName + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasRecordedItemWithHimCondition.class);
	}
}
