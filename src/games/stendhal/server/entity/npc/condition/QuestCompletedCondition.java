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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest completed?
 */
@Dev(category=Category.QUEST_SLOT, label="Completed?")
public class QuestCompletedCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestCompletedCondition.
	 *
	 * @param questname
	 *            name of quest-slot
	 */
	public QuestCompletedCondition(final String questname) {
		this.questname = checkNotNull(questname);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		// FIXME: this should check IQuest.isCompleted
		/*
		final IQuest quest = StendhalQuestSystem.get().getQuestFromSlot(questname);
		if (quest != null) {
			return quest.isCompleted(player);
		}
		*/

		return (player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestCompleted <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return 45779 * questname.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestCompletedCondition)) {
			return false;
		}
		QuestCompletedCondition other = (QuestCompletedCondition) obj;
		return questname.equals(other.questname);
	}
}
