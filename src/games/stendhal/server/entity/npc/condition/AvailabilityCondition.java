/***************************************************************************
 *                 (C) Copyright 2003-2013 - Faiumoni e. V.                *
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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks whether something is available.
 *
 * @author hendrik
 */
public class AvailabilityCondition implements ChatCondition {
	private final AvailabilityChecker checker;

	/**
	 * creates an AvailabilityCondition
	 *
	 * @param checker AvailabilityChecker
	 */
	public AvailabilityCondition(AvailabilityChecker checker) {
		this.checker = checker;
	}


	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return checker.isAvailable();
	}


	@Override
	public int hashCode() {
		return 43627 * checker.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AvailabilityCondition)) {
			return false;
		}
		AvailabilityCondition other = (AvailabilityCondition) obj;
		return checker.equals(other.checker);
	}

	@Override
	public String toString() {
		String temp = checker.toString();
		return "AvailabilityCondition [checker=" + temp.substring(0, Math.min(20, temp.length())) + "]";
	}

}
