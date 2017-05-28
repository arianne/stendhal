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

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the state of a quest to a timestamp,
 * randomly picked between minimum and maximum delay from current system time in minutes
 *
 * @see games.stendhal.server.entity.npc.condition.TimeReachedCondition
 * @see games.stendhal.server.entity.npc.action.SayTimeRemainingUntilTimeReachedAction
 *
 * @author omero
 */
@Dev(category=Category.TIME, label="State")
public class SetQuestToFutureRandomTimeStampAction implements ChatAction {

	private final String questname;
	private final int index;
	private final int min_delay;
	private final int max_delay;

	/**
	 * Creates a new SetQuestToFutureRandomTimeStampAction.
	 *
	 * @param questname name of quest-slot to change
	 * @param min_delay in minutes
	 * @param max_delay in minutes
	 */
	public SetQuestToFutureRandomTimeStampAction(final String questname, final int min_delay, final int max_delay) {
		this.questname = checkNotNull(questname);
		this.index = -1;
		this.min_delay = min_delay;
		this.max_delay = max_delay;
	}

	/**
	 * Creates a new SetQuestToFutureRandomTimeStampAction.
	 *
	 * @param questname name of quest-slot to change
	 * @param index index of sub state
	 * @param minDelay minimum delay in minutes
	 * @param maxDelay maximum delay in minutes
	 */
	@Dev
	public SetQuestToFutureRandomTimeStampAction(final String questname, @Dev(defaultValue="1") final int index, final int minDelay, final int maxDelay) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.min_delay = minDelay;
		this.max_delay = maxDelay;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String timestamp = Long.toString(
				System.currentTimeMillis() + ( Rand.randUniform(min_delay, max_delay) * MathHelper.MILLISECONDS_IN_ONE_MINUTE));
		if (index > -1) {
			player.setQuest(questname, index, timestamp);
		} else {
			player.setQuest(questname, timestamp);
		}
	}

	@Override
	public String toString() {
		return "SetQuestToFutureRandomTimeStampAction<" + questname + "[" + index + "]>";
	}

	@Override
	public int hashCode() {
		return 5507 * (questname.hashCode() + 5519 * index) + min_delay * max_delay;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SetQuestToFutureRandomTimeStampAction)) {
			return false;
		}
		SetQuestToFutureRandomTimeStampAction other = (SetQuestToFutureRandomTimeStampAction) obj;
		return (index == other.index)
			&& (min_delay == other.min_delay)
			&& (max_delay == other.max_delay)
			&& questname.equals(other.questname);
	}
}
