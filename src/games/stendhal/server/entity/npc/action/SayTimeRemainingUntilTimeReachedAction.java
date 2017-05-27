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
import games.stendhal.server.util.TimeUtil;

/**
 * Tells the time remaining from current system time to a timestamp stored in a quest slot
 *
 * @see games.stendhal.server.entity.npc.condition.TimeReachedCondition
 * @see games.stendhal.server.entity.npc.action.SetQuestToFutureRandomTimeStampAction
 * @author omero
 */
@Dev(category = Category.TIME, label="\"...\"")
public class SayTimeRemainingUntilTimeReachedAction implements ChatAction {

	private final String questname;
	private final int index;
	private final String message;

	/**
	 * Creates a new SayTimeRemainingUntilTimeReachedAction.
	 *
	 * @param questname quest slot to check
	 * @param message what to say before stating the approximated remaining time
	 */
	public SayTimeRemainingUntilTimeReachedAction(final String questname, final String message) {
		this.questname = checkNotNull(questname);
		this.index = -1;
		this.message = checkNotNull(message);
	}

	/**
	 * Creates a new SayTimeRemainingUntilTimeReachedAction.
	 *
	 * @param questname quest slot to check
	 * @param index index of sub state
	 * @param message what to say before saying the approximated remaining time
	 */
	@Dev
	public SayTimeRemainingUntilTimeReachedAction(final String questname,
			@Dev(defaultValue = "1") final int index, final String message) {
		this.questname = checkNotNull(questname);
		this.index = index;
		this.message = checkNotNull(message);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

		long timestamp;

		// the player never got the quest
		if (!player.hasQuest(questname)) {
			return;
		}

		if (index > -1) {
			try {
				timestamp = Long.parseLong(player.getQuest(questname, index));
			} catch (final NumberFormatException e) {
				// set to 0 if it was no Long, as if this quest was done at the beginning of time
				timestamp = 0;
			}
		} else {
			try {
				timestamp = Long.parseLong(player.getQuest(questname));
			} catch (final NumberFormatException e) {
				// set to 0 if it was no Long, as if this quest was done at the beginning of time
				timestamp = 0;
			}

		}

		final long timeRemaining = (timestamp - System.currentTimeMillis());
		// trim of white spaces so that the coder doesn't have to remember whether to add a space or
		// not
		raiser.say(message.trim() + " " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
				+ ".");

	}

	@Override
	public String toString() {
		return "SayTimeRemainingUntilTimeReachedAction<" + questname + "[" + index + "] \""
				+ message + "\">";
	}

	@Override
	public int hashCode() {
		return 5393 * (questname.hashCode() + 5407 * (message.hashCode() + 5413 * index));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayTimeRemainingUntilTimeReachedAction)) {
			return false;
		}
		SayTimeRemainingUntilTimeReachedAction other = (SayTimeRemainingUntilTimeReachedAction) obj;
		return (index == other.index)
			&& questname.equals(other.questname)
			&& message.equals(other.message);
	}
}
