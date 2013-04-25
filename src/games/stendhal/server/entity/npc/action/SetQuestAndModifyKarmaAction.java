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

/**
 * Sets the current state of a quest and modifies the karma of the player.
 */
@Dev(category=Category.IGNORE, label="State, Karma")
public class SetQuestAndModifyKarmaAction implements ChatAction {

	private final String questname;
	private final String state;
	private final double karmaDiff;

	/**
	 * Creates a new SetQuestAction.
	 *
	 * @param questname
	 *            name of quest-slot to change
	 * @param state
	 *            new value
	 * @param karmaDiff
	 *            amount of karma to add (negative numbers allowed)
	 */
	public SetQuestAndModifyKarmaAction(final String questname, final String state,
			final double karmaDiff) {
		this.questname = questname;
		this.state = state;
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		player.setQuest(questname, state);
		player.addKarma(karmaDiff);
	}

	@Override
	public String toString() {
		return "SetQuestAndModifyKarma<" + questname + ",\"" + state + "\","
				+ karmaDiff + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(karmaDiff);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		if (questname == null) {
			result = PRIME * result;
		} else {
			result = PRIME * result	+ questname.hashCode();
		}
		if (state == null) {
			result = PRIME * result;
		} else {
			result = PRIME * result + state.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SetQuestAndModifyKarmaAction other = (SetQuestAndModifyKarmaAction) obj;
		if (Double.doubleToLongBits(karmaDiff) != Double.doubleToLongBits(other.karmaDiff)) {
			return false;
		}
		if (questname == null) {
			if (other.questname != null) {
				return false;
			}
		} else if (!questname.equals(other.questname)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		return true;
	}

}
