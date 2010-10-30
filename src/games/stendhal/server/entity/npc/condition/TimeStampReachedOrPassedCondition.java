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

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Check if current system time matches or is past an arbitrary timestamp stored in a quest slot
 * If the quest slot isn't in the expected format, returns true
 */
public class TimeStampReachedOrPassedCondition implements ChatCondition {

	private final String questname;
	private final int index;
    private final boolean requiredstate;
	
	/**
	 * Creates a new TimeStampReachedOrPassedCondition for checking wether or not
     * an arbitrary timestamp in quest slot has been reached or passed by also specifying
     * what should be the required state (true or false).
	 * 
	 * @param questname name of the quest slot to check
     * @param requiredstate to ask wether or not the arbitrary timestamp has been reached or passed
	 */
	public TimeStampReachedOrPassedCondition(final String questname, final boolean requiredstate) {
		this.questname = questname;
		this.index = -1;
		this.requiredstate = requiredstate;
	}

	/**
	 * Creates a new TimeStampReachedOrPassedCondition where timestamp is stored in a quest slot
	 * 
	 * @param questname name of quest slot to check
	 * @param index position of an arbitrary timestamp within the quest slot 'array'
     * @param requiredstate to ask wether or not the arbitrary timestamp has been reached or passed
	 */
	public TimeStampReachedOrPassedCondition(final String questname, final int index, final boolean requiredstate) {
		this.questname = questname;
		this.index = index;
		this.requiredstate = requiredstate;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		long timestamp;

        /**
         * The player has never asked for the quest but consider also requiredstate
         */
		if (!player.hasQuest(questname)) {
			return (true && requiredstate);
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
		return ((timeRemaining <= 0L) && requiredstate);

	}

	@Override
	public String toString() {
		return "TimeStampReachedOrPassedCondition on quest <"
            + questname + "> with required state <" + requiredstate + ">?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TimeStampReachedOrPassedCondition.class);
	}
}
