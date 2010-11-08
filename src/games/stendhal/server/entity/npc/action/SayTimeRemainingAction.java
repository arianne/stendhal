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

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * Tells the time remaining between the timestamp on quest slot + delay time, and now.
 * 
 * Ideally used with TimePassedCondition and SetQuestToTimeStampAction
 */
public class SayTimeRemainingAction implements ChatAction {

	private final String questname;
	private final String message;
	private final int delay;
	private final int arg;

	/**
	 * Creates a new SayTimeRemainingAction.
	 * 
	 * @param questname
	 *            name of quest-slot to check
	 * @param arg
	 *            position of the timestamp within the quest slot 'array'
	 * @param delay
	 *            delay in minutes
	 * @param message
	 *            message to come before statement of remaining time
	 *            
	 */
	public SayTimeRemainingAction(final String questname, final int arg,
			final int delay, final String message) {
		this.questname = questname;
		this.message = message;
		this.delay = delay;
		this.arg = arg;
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

	public SayTimeRemainingAction(final String questname, final int delay,
			final String message) {
		this.questname = questname;
		this.message = message;
		this.delay = delay;
		this.arg = 0;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (!player.hasQuest(questname)) {
			return;
		} else {
			final String[] tokens = player.getQuest(questname).split(";"); 
			final long delayInMilliseconds = delay * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
		
			// timeRemaining is ''time when quest was done +
			// delay - time now''
			// if this is > 0, the time has not yet passed
			final long timeRemaining = (Long.parseLong(tokens[arg]) + delayInMilliseconds)
				- System.currentTimeMillis();
			// TODO: return an error if tokens.length < arg 
			// TODO: catch the number format exception in case tokens[arg] is no number? or does parseLong do this?
			raiser.say(message + " " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
		}
	}

	@Override
	public String toString() {
		return "SayTimeRemainingAction<" + questname + ",\"" + message + "\","
				 + "\"," + delay + ">";
	}
	

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayTimeRemainingAction.class);
	}

	

}
