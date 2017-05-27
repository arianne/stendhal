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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * An inverse condition.
 */
@Dev(category=Category.LOGIC, label="Not")
public class NotCondition implements ChatCondition {

	private final ChatCondition condition;

	/**
	 * Creates a new "not"-condition.
	 *
	 * @param condition
	 *            condition which result is to be inversed
	 */
	public NotCondition(final ChatCondition condition) {
		this.condition = checkNotNull(condition);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return !condition.fire(player, sentence, entity);
	}

	@Override
	public String toString() {
		return "NOT <" + condition + ">";
	}

	@Override
	public int hashCode() {
		return 43777 * condition.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof NotCondition)) {
			return false;
		}
		NotCondition other = (NotCondition) obj;
		return condition.equals(other.condition);
	}
}
