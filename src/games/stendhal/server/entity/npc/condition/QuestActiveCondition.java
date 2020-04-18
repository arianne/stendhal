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
 * Was this quest started but not completed?
 */
@Dev(category=Category.QUEST_SLOT, label="Active?")
public class QuestActiveCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestActiveCondition.
	 *
	 * @param questname
	 *            name of quest slot
	 */
	public QuestActiveCondition(final String questname) {
		this.questname = checkNotNull(questname);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		// FIXME: this should check IQuest.isCompleted
		return (player.hasQuest(questname) && !player.isQuestInState(questname, 0, "rejected") && !player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestActive <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return 45767 * questname.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestActiveCondition)) {
			return false;
		}
		QuestActiveCondition other = (QuestActiveCondition) obj;
		return questname.equals(other.questname);
	}
}
