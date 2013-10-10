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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Is the player's level smaller than the specified one?
 */
@Dev(category=Category.IGNORE, label="Level?")
public class LevelLessThanCondition implements ChatCondition {

	private final int level;

	/**
	 * Creates a new LevelGreaterThanCondition.
	 *
	 * @param level
	 *            level
	 */
	public LevelLessThanCondition(final int level) {
		this.level = level;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getLevel() < level);
	}

	@Override
	public String toString() {
		return "level < " + level + " ";
	}

	@Override
	public int hashCode() {
		return 43721 * level;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof LevelLessThanCondition)) {
			return false;
		}
		LevelLessThanCondition other = (LevelLessThanCondition) obj;
		return level == other.level;
	}

}
