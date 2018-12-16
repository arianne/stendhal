/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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
import games.stendhal.server.util.Area;

/**
 * Checks if there is room for a player within a restricted area.
 */
@Dev(category=Category.LOCATION, label="Area full?")
public class AreaIsFullCondition implements ChatCondition {

	/** area to check */
	private final Area area;
	/** maximum occupancy */
	private final int max;


	public AreaIsFullCondition(final Area area, final int max) {
		this.area = area;
		this.max = max;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return area.getPlayers().size() >= max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + max;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AreaIsFullCondition other = (AreaIsFullCondition) obj;
		if (area == null) {
			if (other.area != null) {
				return false;
			}
		} else if (!area.equals(other.area)) {
			return false;
		}
		if (max != other.max) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AreaIsFullCondition [area=" + area + ", max=" + max + "]";
	}
}
