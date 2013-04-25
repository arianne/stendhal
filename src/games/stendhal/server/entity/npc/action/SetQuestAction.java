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
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Sets the current state of a quest.
 */
@Dev(category=Category.QUEST_SLOT, label="State")
public class SetQuestAction implements ChatAction {

	private final String questname;
	private final int index;
	private final String state;

	/**
	 * Creates a new SetQuestAction.
	 *
	 * @param questname
	 *            name of quest-slot to change
	 * @param state
	 *            new value
	 */
	public SetQuestAction(final String questname, final String state) {
		this.questname = questname;
		this.index = -1;
		this.state = state;
	}

	/**
	 * Creates a new SetQuestAction.
	 *
	 * @param questname
	 *            name of quest-slot to change
	 * @param index
	 *            index of sub state
	 * @param state
	 *            new value
	 */
	@Dev
	public SetQuestAction(final String questname, final int index, final String state) {
		this.questname = questname;
		this.index = index;
		this.state = state;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (index > -1) {
			player.setQuest(questname, index, state);
		} else {
			player.setQuest(questname, state);
		}
	}

	@Override
	public String toString() {
		return "SetQuest<" + questname + "[" + index + "] = " + state + ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SetQuestAction.class);
	}
}
