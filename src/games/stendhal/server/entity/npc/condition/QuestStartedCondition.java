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
 * Was this quest at least started? See QuestActiveCondition to check that it was started but not completed.
 */
@Dev(category=Category.QUEST_SLOT, label="Started?")
public class QuestStartedCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestStartedCondition.
	 *
	 * @param questname
	 *            name of quest slot
	 */
	public QuestStartedCondition(final String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.hasQuest(questname) && !"rejected".equals(player.getQuest(questname, 0)));
	}

	@Override
	public String toString() {
		return "QuestStarted <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}
