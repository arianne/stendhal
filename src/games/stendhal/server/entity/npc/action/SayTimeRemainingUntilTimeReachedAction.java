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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * Tells the time remaining from current system time to a timestamp stored in a quest slot
 * 
 * Ideally used with SetQuestToFutureRandomTimeStampAction  (optionally) and TimeReachedCondition 
 * 
 * @author omero
 */
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
		this.questname = questname;
		this.index = -1;
		this.message = message;
	}

	/**
	 * Creates a new SayTimeRemainingUntilTimeReachedAction.
	 * 
	 * @param questname quest slot to check
	 * @param index position of the timestamp within the quest slot 'array'
	 * @param message what to say before stating the approximated remaining time
	 */
	public SayTimeRemainingUntilTimeReachedAction(final String questname, final int index, final String message) {
		this.questname = questname;
		this.index = index;
		this.message = message;
	} 

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
        raiser.say(message + " " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");

	}

	@Override
	public String toString() {
		return "SayTimeRemainingUntilTimeReachedAction<" + questname + "[" + index + "] \"" + message + "\">";
	}
	

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayTimeRemainingUntilTimeReachedAction.class);
	}

	

}
