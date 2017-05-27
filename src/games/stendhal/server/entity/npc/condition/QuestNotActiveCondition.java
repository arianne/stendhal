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
 * Is this quest either unknown, completed or rejected?
 */
@Dev(category=Category.IGNORE)
public class QuestNotActiveCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestNotActiveCondition.
	 *
	 * @param questname
	 *            name of quest slot
	 */
	public QuestNotActiveCondition(final String questname) {
		this.questname = checkNotNull(questname);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (!player.hasQuest(questname) || player.isQuestInState(questname, 0, "rejected") || player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestNotActive <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return 45823 * questname.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestNotActiveCondition)) {
			return false;
		}
		QuestNotActiveCondition other = (QuestNotActiveCondition) obj;
		return questname.equals(other.questname);
	}
}
