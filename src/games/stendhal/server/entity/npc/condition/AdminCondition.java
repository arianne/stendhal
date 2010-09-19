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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player an admin?
 */
public class AdminCondition implements ChatCondition {

	private final int requiredAdminlevel;

	/**
	 * Creates a new AdminCondition for high level admins. '
	 */
	public AdminCondition() {
		requiredAdminlevel = 5000;
	}

	/**
	 * Creates a new AdminCondition.
	 * 
	 * @param requiredAdminlevel
	 *            minimum admin level '
	 */
	public AdminCondition(final int requiredAdminlevel) {
		this.requiredAdminlevel = requiredAdminlevel;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getAdminLevel() >= requiredAdminlevel);
	}

	@Override
	public String toString() {
		return "admin <" + requiredAdminlevel + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				AdminCondition.class);
	}

}
