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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Has the player killed at least one of this creature, with or without the help of any other player.
 * 
 * @author hendrik
 */
public class KilledCondition implements ChatCondition {
	private final Set<String> toKill;

	/**
	 * creates a new KilledCondition.
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				KilledCondition.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
