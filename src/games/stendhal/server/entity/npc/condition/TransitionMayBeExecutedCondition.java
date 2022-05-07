/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import java.util.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * checks whether a transition may be executed, e. g. there is no transition
 * with a higher priority available.
 *
 * @author hendrik
 *
 */
public class TransitionMayBeExecutedCondition implements ChatCondition {
	private Object owner;

	/**
	 * creates a TransitionMayBeExecuted
	 * 
	 * @param owner
	 */
	public TransitionMayBeExecutedCondition(Object owner) {
		this.owner = owner;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		if (!(npc instanceof SpeakerNPC)) {
			return true;
		}
		return ((SpeakerNPC) npc).mayGreetingConditionBeExecuted(owner, player, sentence);
	}

	@Override
	public String toString() {
		return "TransitionMayBeExecutedCondition<" + owner.getClass().getName() + ">";
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TransitionMayBeExecutedCondition other = (TransitionMayBeExecutedCondition) obj;
		return Objects.equals(owner, other.owner);
	}

}
