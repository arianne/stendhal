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
 * This condition returns always true. Use it in a quest file to override
 * behaviour defined in the map file
 */
@Dev(category=Category.LOGIC, label="True!")
public class AlwaysTrueCondition implements ChatCondition {

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return true;
	}

	@Override
	public String toString() {
		return "true";
	}

	@Override
	public int hashCode() {
		return 43609;
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof AlwaysTrueCondition);
	}
}
