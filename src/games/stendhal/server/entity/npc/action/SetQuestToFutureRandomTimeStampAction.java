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

import games.stendhal.common.Rand;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Sets the state of a quest to an arbitrary timestamp
 *
 * @author omero
 */
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
		this.questname = questname;
		this.index = -1;
		this.min_delay = min_delay;
		this.max_delay = max_delay;
	}

	/**
	 * Creates a new SetQuestToFutureRandomTimeStampAction.
	 * 
	 * @param questname name of quest-slot to change
	 * @param index index of sub state
	 * @param min_delay minimum delay in minutes
	 * @param max_delay maximum delay in minutes
	 */
	public SetQuestToFutureRandomTimeStampAction(final String questname, final int index, final int min_delay, final int max_delay) {
		this.questname = questname;
		this.index = index;
		this.min_delay = min_delay;
		this.max_delay = max_delay;
	}

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
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SetQuestToFutureRandomTimeStampAction.class);
	}
}
