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
 * Check if current system time reached a timestamp stored in a quest slot.
 * If the quest slot isn't in the expected format, returns true
 *
 * @see games.stendhal.server.entity.npc.action.SayTimeRemainingUntilTimeReachedAction
 * @see games.stendhal.server.entity.npc.action.SetQuestToFutureRandomTimeStampAction
 *
 * @author omero
 */
@Dev(category=Category.TIME, label="Time?")
public class TimeReachedCondition implements ChatCondition {

	private final String questname;
	private final int index;

	/**
	 * Creates a new TimeReachedCondition for checking wether or not a timestamp in quest slot has been reached
	 *
	 * @param questname name of the quest slot to check
	 */
	public TimeReachedCondition(final String questname) {
		this.questname = checkNotNull(questname);
		this.index = -1;
	}

	/**
	 * Creates a new TimeReachedCondition for checking wether or not a timestamp in quest slot has been reached
	 *
	 * @param questname name of quest slot to check
	 * @param index index of sub state
	 */
	@Dev
	public TimeReachedCondition(final String questname, final int index) {
		this.questname = checkNotNull(questname);
		this.index = index;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		long timestamp;

		// The player never did the quest, assume the time is right for taking it now
		if (!player.hasQuest(questname)) {
			return true;
		}

		if (index > -1) {
			try {
				timestamp = Long.parseLong(player.getQuest(questname, index));
			} catch (final NumberFormatException e) {
				// set to 0 if it was no Long, as if this quest was done at the beginning of time.
				timestamp = 0;
			}
		} else {
			try {
				timestamp = Long.parseLong(player.getQuest(questname));
			} catch (final NumberFormatException e) {
				// set to 0 if it was no Long, as if this quest was done at the beginning of time.
				timestamp = 0;
			}
		}

		final long timeRemaining = (timestamp - System.currentTimeMillis());
		return (timeRemaining <= 0L);

	}

	@Override
	public String toString() {
		return "TimeReachedCondition<" + questname + "[" + index + "]>";
	}

	@Override
	public int hashCode() {
		return 5051 * questname.hashCode() + index;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TimeReachedCondition)) {
			return false;
		}
		TimeReachedCondition other = (TimeReachedCondition) obj;
		return (index == other.index)
			&& questname.equals(other.questname);
	}
}
