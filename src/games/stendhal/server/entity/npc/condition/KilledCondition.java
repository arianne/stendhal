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
package games.stendhal.server.entity.npc.condition;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Has the player killed at least one of each specified creature, with or without the help of any other player.
 *
 * @author hendrik
 */
@Dev(category=Category.KILLS, label="Kills?")
public class KilledCondition implements ChatCondition {
	private final Set<String> toKill;

	/**
	 * creates a new KilledCondition.
	 *
	 * @param toKill
	 *            list of creatures for which the player need to have participated in killing at least one each
	 */
	@Dev
	public KilledCondition(final List<String> toKill) {
		this.toKill = new TreeSet<String>(toKill);
	}

	/**
	 * creates a new KilledCondition.
	 *
	 * @param toKill
	 *            creatures which should be killed by the player
	 */
	public KilledCondition(final String... toKill) {
		this.toKill = new TreeSet<String>(Arrays.asList(toKill));
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		for (final String creature : toKill) {
			if (!player.hasKilled(creature)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "KilledCondition <" + toKill + ">";
	}

	@Override
	public int hashCode() {
		return 43661 * toKill.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KilledCondition)) {
			return false;
		}
		KilledCondition other = (KilledCondition) obj;
		return toKill.equals(other.toKill);
	}

}
