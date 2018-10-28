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
}
