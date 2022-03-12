/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;


/**
 * Factory for experience achievements
 *
 * @author madmetzger
 */
public class ExperienceAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_GREENHORN = "xp.level.010";
	public static final String ID_NOVICE = "xp.level.050";
	public static final String ID_APPRENTICE = "xp.level.100";
	public static final String ID_ADVENTURER = "xp.level.200";
	public static final String ID_EXPERIENCED_ADV = "xp.level.300";
	public static final String ID_MASTER_ADV = "xp.level.400";
	public static final String ID_MASTER = "xp.level.500";
	public static final String ID_HIGH_MASTER = "xp.level.597";


	@Override
	protected Category getCategory() {
		return Category.EXPERIENCE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_GREENHORN, "Greenhorn",
			"Reach level 10",
			Achievement.EASY_BASE_SCORE, true,
			new LevelGreaterThanCondition(9)));

		achievements.add(createAchievement(
			ID_NOVICE, "Novice",
			"Reach level 50",
			Achievement.EASY_BASE_SCORE, true,
			new LevelGreaterThanCondition(49)));

		achievements.add(createAchievement(
			ID_APPRENTICE, "Apprentice",
			"Reach level 100",
			Achievement.EASY_BASE_SCORE, true,
			new LevelGreaterThanCondition(99)));

		achievements.add(createAchievement(
			ID_ADVENTURER, "Adventurer",
			"Reach level 200",
			Achievement.MEDIUM_BASE_SCORE, true,
			new LevelGreaterThanCondition(199)));

		achievements.add(createAchievement(
			ID_EXPERIENCED_ADV, "Experienced Adventurer",
			"Reach level 300",
			Achievement.MEDIUM_BASE_SCORE, true,
			new LevelGreaterThanCondition(299)));

		achievements.add(createAchievement(
			ID_MASTER_ADV, "Master Adventurer",
			"Reach level 400",
			Achievement.MEDIUM_BASE_SCORE, true,
			new LevelGreaterThanCondition(399)));

		achievements.add(createAchievement(
			ID_MASTER, "Stendhal Master",
			"Reach level 500",
			Achievement.HARD_BASE_SCORE, true,
			new LevelGreaterThanCondition(499)));

		achievements.add(createAchievement(
			ID_HIGH_MASTER, "Stendhal High Master",
			"Reach level 597",
			Achievement.HARD_BASE_SCORE, true,
			new LevelGreaterThanCondition(596)));

		return achievements;
	}
}
