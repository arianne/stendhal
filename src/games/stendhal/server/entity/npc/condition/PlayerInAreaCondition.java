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
import games.stendhal.server.util.Area;

/**
 * Is the player in the specified area?
 */
@Dev(category=Category.LOCATION, label="Zone?")
public class PlayerInAreaCondition implements ChatCondition {

	private final Area area;

	/**
	 * Creates a new PlayerInAreaCondition.
	 *
	 * @param area
	 *            Area
	 */
	public PlayerInAreaCondition(final Area area) {
		this.area = checkNotNull(area);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return area.contains(player);
	}

	@Override
	public String toString() {
		return "player in <" + area + ">";
	}

	@Override
	public int hashCode() {
		return 43969 * area.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerInAreaCondition)) {
			return false;
		}
		PlayerInAreaCondition other = (PlayerInAreaCondition) obj;
		return area.equals(other.area);
	}
}
