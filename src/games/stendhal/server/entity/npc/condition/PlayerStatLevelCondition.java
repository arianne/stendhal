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

/**
 * Compares an integer value attribution.
 */
@Dev(category=Category.STATS, label="playerStats")
public class PlayerStatLevelCondition implements ChatCondition {

	private final String attribute;
	private final ComparisonOperator comparisonOperator;
	private final int targetLevel;

	public PlayerStatLevelCondition(final String attribute, final ComparisonOperator comparisonOperator, final int targetLevel) {
		this.attribute = attribute;
		this.comparisonOperator = comparisonOperator;
		this.targetLevel = targetLevel;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		if (!player.has(attribute)) {
			return false;
		}

		int statLevel = player.getInt(attribute);
		return comparisonOperator.compare(statLevel, targetLevel);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((comparisonOperator == null) ? 0 : comparisonOperator.hashCode());
		result = prime * result + targetLevel;
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
		PlayerStatLevelCondition other = (PlayerStatLevelCondition) obj;
		if (attribute == null) {
			if (other.attribute != null) {
				return false;
			}
		} else if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (comparisonOperator == null) {
			if (other.comparisonOperator != null) {
				return false;
			}
		} else if (!comparisonOperator.equals(other.comparisonOperator)) {
			return false;
		}
		if (targetLevel != other.targetLevel) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PlayerStatLevelCondition [attribute=" + attribute + ", expression=" + comparisonOperator + ", targetLevel="
				+ targetLevel + "]";
	}


}
