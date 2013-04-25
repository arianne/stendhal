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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
		this.questname = questname;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.hasQuest(questname) && !player.isQuestInState(questname, 0, "rejected") && !player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestActive <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestActiveCondition.class);
	}
}
