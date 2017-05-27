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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the state of a quest to the current timestamp.
 *
 * @see games.stendhal.server.entity.npc.condition.TimePassedCondition
 * @see games.stendhal.server.entity.npc.action.SayTimeRemainingAction
 */
@Dev(category=Category.TIME, label="State")
public class SetQuestToTimeStampAction implements ChatAction {

	private final String questname;
	private final int index;

	/**
	 * Creates a new SetQuestToTimeStampAction.
	 *
	 * @param questname name of quest-slot to change
	 */
	public SetQuestToTimeStampAction(final String questname) {
		this.questname = checkNotNull(questname);
		this.index = -1;
	}

	/**
	 * Creates a new SetQuestToTimeStampAction.
	 *
	 * @param questname name of quest-slot to change
	 * @param index index of sub state
	 */
	@Dev
	public SetQuestToTimeStampAction(final String questname, @Dev(defaultValue="1") final int index) {
		this.questname = checkNotNull(questname);
		this.index = index;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String state = Long.toString(System.currentTimeMillis());
		if (index > -1) {
			player.setQuest(questname, index, state);
		} else {
			player.setQuest(questname, state);
		}
	}

	@Override
	public String toString() {
		return "SetQuestToTimeStampAction<" + questname + "[" + index + "]>";
	}

	@Override
	public int hashCode() {
		return 5531 * (questname.hashCode() + 5557 * index);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SetQuestToTimeStampAction)) {
			return false;
		}
		SetQuestToTimeStampAction other = (SetQuestToTimeStampAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname);
	}
}
