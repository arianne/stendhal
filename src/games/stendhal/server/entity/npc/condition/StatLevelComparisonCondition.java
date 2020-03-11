/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;


public class StatLevelComparisonCondition implements ChatCondition {

	private final static Logger logger = Logger.getLogger(StatLevelComparisonCondition.class);

	private final String statName;
	private final String comparisonOperator;
	private final int targetLevel;


	public StatLevelComparisonCondition(final String stat, final String operator, final int level) {
		statName = stat;
		comparisonOperator = operator;
		targetLevel = level;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		if (!player.has(statName)) {
			logger.warn("Player stat not defined: " + statName);
			return false;
		}

		final int statLevel = player.getInt(statName);

		switch (comparisonOperator) {
		case "==":
			return statLevel == targetLevel;
		case "!=":
			return statLevel != targetLevel;
		case ">":
			return statLevel > targetLevel;
		case "<":
			return statLevel < targetLevel;
		case ">=":
			return statLevel >= targetLevel;
		case "<=":
			return statLevel <= targetLevel;
		default:
			return false;
		}
	}
}
