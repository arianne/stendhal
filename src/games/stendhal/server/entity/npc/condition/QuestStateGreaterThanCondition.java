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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
/**
 * Condition to check if the value in a quest slot is greater than an expected value. If the value is not a number, it is treated as 0.
 *
 * @author madmetzger
 */
@Dev(category=Category.QUEST_SLOT, label="State?")
public class QuestStateGreaterThanCondition implements ChatCondition {

	/**
	 * expected value to compare against
	 */
	private final int expectedSmallerValue;

	/**
	 * at which index is the number of finishings stored in the quest slot
	 */
	private final int index;

	/**
	 * which quest should be checked
	 */
	private final String questname;

	/**
	 * Create a new QuestStateGreaterThanCondition
	 * @param quest name of the quest slot
	 * @param index index where the number is stored in the quest slot
	 * @param expectedSmallerValue expected smaller value to compare
	 */
	public QuestStateGreaterThanCondition(String quest, int index, int expectedSmallerValue) {
		this.questname = checkNotNull(quest);
		this.expectedSmallerValue = expectedSmallerValue;
		this.index = index;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		if(player.hasQuest(questname)) {
			String questState = player.getQuest(questname);
			String[] content = questState.split(";");
			if (content.length - 1 < index) {
				return false;
			}
			int actualNumber = MathHelper.parseIntDefault(content[index], 0);
			return actualNumber > expectedSmallerValue;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 45943 * questname.hashCode() + 45949 * index + 45953 * expectedSmallerValue;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestStateGreaterThanCondition)) {
			return false;
		}
		QuestStateGreaterThanCondition other = (QuestStateGreaterThanCondition) obj;
		return (index == other.index)
			&& (expectedSmallerValue == other.expectedSmallerValue)
			&& questname.equals(other.questname);
	}
}
