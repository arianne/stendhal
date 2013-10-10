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
 * Is the player's level greater than the specified one?
 */
@Dev(category=Category.STATS, label="Level?")
public class LevelGreaterThanCondition implements ChatCondition {

	private final int level;

	/**
	 * Creates a new LevelGreaterThanCondition.
	 *
	 * @param level
	 *            level
	 */
	public LevelGreaterThanCondition(final int level) {
		this.level = level;
	}

	/**
	 * @return true if players level greater then conditions Level
	 */
	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getLevel() > level);
	}

	@Override
	public String toString() {
		return "level > " + level + " ";
	}

	@Override
	public int hashCode() {
		return 43717 * level;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof LevelGreaterThanCondition)) {
			return false;
		}
		LevelGreaterThanCondition other = (LevelGreaterThanCondition) obj;
		return level == other.level;
	}

}
