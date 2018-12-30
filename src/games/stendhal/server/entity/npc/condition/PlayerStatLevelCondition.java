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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Compares an integer value attribution.
 */
public class PlayerStatLevelCondition implements ChatCondition {

	final String attribute;
	final String expression;
	final int targetLevel;

	public PlayerStatLevelCondition(final String attribute, final String expression, final int targetLevel) {
		this.attribute = attribute;
		this.expression = expression;
		this.targetLevel = targetLevel;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		if (!player.has(attribute)) {
			return false;
		}

		final int statLevel = player.getInt(attribute);

		switch (expression) {
		case "eq":
			return statLevel == targetLevel;
		case "lt":
			return statLevel < targetLevel;
		case "gt":
			return statLevel > targetLevel;
		case "lteq":
			return statLevel <= targetLevel;
		case "gteq":
			return statLevel >= targetLevel;
		default:
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
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
		if (expression == null) {
			if (other.expression != null) {
				return false;
			}
		} else if (!expression.equals(other.expression)) {
			return false;
		}
		if (targetLevel != other.targetLevel) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PlayerStatLevelCondition [attribute=" + attribute + ", expression=" + expression + ", targetLevel="
				+ targetLevel + "]";
	}


}
