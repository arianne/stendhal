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
 * Sets the state of a quest to the current age of the player.
 */
@Dev(category=Category.STATS, label="State")
public class SetQuestToPlayerAgeAction implements ChatAction {

	private final String questname;
	private final int index;

	/**
	 * Creates a new SetQuestToPlayerAgeAction.
	 *
	 * @param questname name of quest-slot to change
	 */
	public SetQuestToPlayerAgeAction(final String questname) {
		this.questname = checkNotNull(questname);
		this.index = -1;
	}

	/**
	 * Creates a new SetQuestToPlayerAgeAction.
	 *
	 * @param questname name of quest-slot to change
	 * @param index index of sub state
	 */
	@Dev
	public SetQuestToPlayerAgeAction(final String questname, final int index) {
		this.questname = checkNotNull(questname);
		this.index = index;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String state = Integer.toString(player.getAge());
		if (index > -1) {
			player.setQuest(questname, index, state);
		} else {
			player.setQuest(questname, state);
		}
	}

	@Override
	public String toString() {
		return "SetQuestToPlayerAgeAction<" + questname + "[" + index + "]>";
	}

	@Override
	public int hashCode() {
		return 5521 * (questname.hashCode() + 5527 * index);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SetQuestToPlayerAgeAction)) {
			return false;
		}
		SetQuestToPlayerAgeAction other = (SetQuestToPlayerAgeAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname);
	}
}
