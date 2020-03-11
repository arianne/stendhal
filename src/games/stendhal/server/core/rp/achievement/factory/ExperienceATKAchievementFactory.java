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
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.StatLevelComparisonCondition;


public class ExperienceATKAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.EXPERIENCE_ATK;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final List<Achievement> achievements = new LinkedList<>();

		String level = "25";
		achievements.add(createAchievement(
				"atk.level." + level, "Novice Aggressor", "Reach ATK level " + level,
				Achievement.EASY_BASE_SCORE, true,
				createComparison(level)));

		level = "50";
		achievements.add(createAchievement(
				"atk.level." + level, "Aggressor", "Reach ATK level " + level,
				Achievement.EASY_BASE_SCORE, true,
				createComparison(level)));

		level = "75";
		achievements.add(createAchievement(
				"atk.level." + level, "Advanced Aggressor", "Reach ATK level " + level,
				Achievement.MEDIUM_BASE_SCORE, true,
				createComparison(level)));

		level = "100";
		achievements.add(createAchievement(
				"atk.level." + level, "Expert Aggressor", "Reach ATK level " + level,
				Achievement.MEDIUM_BASE_SCORE, true,
				createComparison(level)));

		level = "150";
		achievements.add(createAchievement(
				"atk.level." + level, "Master Aggressor", "Reach ATK level " + level,
				Achievement.HARD_BASE_SCORE, true,
				createComparison(level)));

		return achievements;
	}

	private StatLevelComparisonCondition createComparison(final String level) {
		return new StatLevelComparisonCondition("atk", ">=", Integer.parseInt(level));
	}
}
