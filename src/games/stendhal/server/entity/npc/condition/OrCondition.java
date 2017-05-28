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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * An or condition.
 */
@Dev(category=Category.LOGIC, label="Or")
public class OrCondition implements ChatCondition {

	private final List<ChatCondition> conditions;

	/**
	 * Creates a new "or"-condition.
	 *
	 * @param condition
	 *            condition which should be or-ed.
	 */
	public OrCondition(final ChatCondition... condition) {
		this.conditions = Arrays.asList(condition);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		for (final ChatCondition condition : conditions) {
			final boolean res = condition.fire(player, sentence, entity);
			if (res) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "or <" + conditions.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 43781 * conditions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OrCondition)) {
			return false;
		}
		OrCondition other = (OrCondition) obj;
		return conditions.equals(other.conditions);
	}
}
