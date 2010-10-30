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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Check if current system time matches or is past a timestamp stored in a quest slot
 * If the quest slot isn't in the expected format, returns true
 */
public class TimeReachedCondition implements ChatCondition {

	private final String questname;
	private final int index;
	
	/**
	 * Creates a new TimeReachedCondition for checking wether or not
     * a timestamp in quest slot has been reached or passed by also specifying
     * what should be the required state (true or false).
	 * 
	 * @param questname name of the quest slot to check
     * @param requiredstate to ask wether or not the arbitrary timestamp has been reached or passed
	 */
	public TimeReachedCondition(final String questname) {
		this.questname = questname;
		this.index = -1;
	}

	/**
	 * Creates a new TimeReachedCondition where timestamp is stored in a quest slot
	 * 
	 * @param questname name of quest slot to check
	 * @param index position of a timestamp within the quest slot 'array'
     * @param requiredstate to ask wether or not the arbitrary timestamp has been reached or passed
	 */
	public TimeReachedCondition(final String questname, final int index) {
		this.questname = questname;
		this.index = index;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		long timestamp;

        /**
         * The player never did it 
         */
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
		return "timestamp reached?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TimeReachedCondition.class);
	}
}
