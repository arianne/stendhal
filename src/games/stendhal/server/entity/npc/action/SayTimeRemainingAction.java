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

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * Tells the time remaining between the timestamp on quest slot + delay time, and now.
 *
 * @see games.stendhal.server.entity.npc.condition.TimePassedCondition
 * @see games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction *
 */
@Dev(category=Category.TIME, label="\"...\"")
public class SayTimeRemainingAction implements ChatAction {

	private static final Logger logger = Logger.getLogger(SayTimeRemainingAction.class);

	private final String questname;
	private final String message;
	private String secondMessage = null;
	private final int delay;
	private final int index;

	/**
	 * Creates a new SayTimeRemainingAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param index
	 *            index of sub state
	 * @param delay
	 *            delay in minutes
	 * @param message
	 *            message to come before statement of remaining time
	 *
	 */
	@Dev
	public SayTimeRemainingAction(final String questname, @Dev(defaultValue="1") final int index, final int delay, final String message) {
		this.questname = checkNotNull(questname);
		this.message = checkNotNull(message);
		this.delay = delay;
		this.index = index;
	}

	/**
	 * Creates a new SayTimeRemainingAction.
	 *
	 * @param questname
	 *            name of quest-slot to check
	 * @param delay
	 *            delay in minutes
	 * @param message
	 *            message to come before statement of remaining time
	 *
	 */

	public SayTimeRemainingAction(final String questname, final int delay, final String message) {
		this.questname = checkNotNull(questname);
		this.message = checkNotNull(message);
		this.delay = delay;
		this.index = 0;
	}

	/**
	 *
	 * @param questname
	 * 		Name of quest slot to check.
	 * @param index
	 * 		Index of sub-state.
	 * @param delay
	 * 		Delay in minutes.
	 * @param message
	 * 		Message to come before statement of remaining time.
	 * @param secondMessage
	 * 		Message to come after statement of remaining time.
	 */
	@Dev
	public SayTimeRemainingAction(final String questname, @Dev(defaultValue="1") final int index, final int delay, final String message,
			final String secondMessage) {
		this(questname, index, delay, message);
		this.secondMessage = secondMessage;
	}

	/**
	 *
	 * @param questname
	 * 		Name of quest slot to check.
	 * @param delay
	 * 		Delay in minutes.
	 * @param message
	 * 		Message to come before statement of remaining time.
	 * @param secondMessage
	 * 		Message to come after statement of remaining time.
	 */
	public SayTimeRemainingAction(final String questname, final int delay, final String message, final String secondMessage) {
		this(questname, delay, message);
		this.secondMessage = secondMessage;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (!player.hasQuest(questname)) {
			return;
		} else {
			final String[] tokens = player.getQuest(questname).split(";");
			final long delayInMilliseconds = delay * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
			if (tokens.length - 1 < index) {
				logger.warn("Incorrect argument " + index + " given for quest slot " + questname);
				return;
			}
			// timeRemaining is ''time when quest was done +
			// delay - time now''
			// if this is > 0, the time has not yet passed
			final long timeRemaining = (MathHelper.parseLong(tokens[index]) + delayInMilliseconds)
				- System.currentTimeMillis();
			// MathHelper.parseLong will catch the number format exception in case tokens[arg] is no number and return 0
			// we trim the message of whitespace so that if the developer added a space at the end we don't now duplicate it
			String msg = message.trim() + " " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".";
			if (secondMessage != null) {
				msg += " " + secondMessage.trim();
			}
			raiser.say(msg);
		}
	}

	@Override
	public String toString() {
		return "SayTimeRemainingAction<" + questname + "[" + index + "],\"" + message + "\","
				 + "\"," + delay + ">";
	}

	@Override
	public int hashCode() {
		return 5419 * (questname.hashCode() + 5431 * (message.hashCode() + 5437 * (index + 5441 * delay)));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayTimeRemainingAction)) {
			return false;
		}
		SayTimeRemainingAction other = (SayTimeRemainingAction) obj;
		return (index == other.index)
			&& (delay == other.delay)
			&& questname.equals(other.questname)
			&& message.equals(other.message);
	}
}
