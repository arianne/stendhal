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
 * Is this quest in this state?
 */
@Dev(category=Category.QUEST_SLOT, label="State?")
public class QuestInStateCondition implements ChatCondition {

	private final String questname;
	private final String state;
	private final int index;

	/**
	 * Creates a new QuestInStateCondition.
	 *
	 * @param questname
	 *            name of quest-slot
	 * @param state
	 *            state
	 */
	public QuestInStateCondition(final String questname, final String state) {
		this.questname = checkNotNull(questname);
		this.index = -1;
		this.state = checkNotNull(state);
	}


	/**
	 * Creates a new QuestInStateCondition.
	 *
	 * @param questname
	 *            name of quest-slot
	 * @param index
	 *            index of sub state
	 * @param state
	 *            state
	 */
	@Dev
	public QuestInStateCondition(final String questname, @Dev(defaultValue="0") final int index, final String state) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.state = checkNotNull(state);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasQuest(questname)) {
			return false;
		}
		if (index > -1) {
			return player.getQuest(questname, index).equals(state);
		} else {
			return player.getQuest(questname).equals(state);
		}
	}

	@Override
	public String toString() {
		return "QuestInState <" + questname + "[" + index + "] = " + state + ">";
	}

	@Override
	public int hashCode() {
		return 45817 * questname.hashCode() + 45821 * index + state.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestInStateCondition)) {
			return false;
		}
		QuestInStateCondition other = (QuestInStateCondition) obj;
		return (index == other.index)
			&& questname.equals(other.questname)
			&& state.equals(other.state);
	}
}
