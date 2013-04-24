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
package games.stendhal.server.core.rp.achievement.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was a quest with this prefix completed?
 */
public class QuestWithPrefixCompletedCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestWithPrefixCompletedCondition.
	 * 
	 * @param questname
	 *            name of quest-slot
	 */
	public QuestWithPrefixCompletedCondition(final String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		List<String> quests = player.getQuests();
		for (String quest : quests) {
			if (quest.startsWith(questname)) {
				if (player.isQuestCompleted(quest)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "QuestWithPrefixCompleted <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestWithPrefixCompletedCondition.class);
	}
}
