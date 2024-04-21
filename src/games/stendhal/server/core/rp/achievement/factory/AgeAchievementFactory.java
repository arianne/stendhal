/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.AgeGreaterThanCondition;


public class AgeAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.AGE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			"age.hours.00024", "Cutting Teeth",
			"Accumulate 24 hours of play time",
			Achievement.EASY_BASE_SCORE, true,
			new AgeGreaterThanCondition(MathHelper.MINUTES_IN_ONE_DAY - 1)));

		achievements.add(createAchievement(
			"age.hours.00168", "Adolescent",
			"Accumulate a week of play time",
			Achievement.EASY_BASE_SCORE, true,
			new AgeGreaterThanCondition(MathHelper.MINUTES_IN_ONE_WEEK - 1)));

		return achievements;
	}
}
