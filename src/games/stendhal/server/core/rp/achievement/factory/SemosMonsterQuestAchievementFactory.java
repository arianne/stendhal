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
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;


/**
 * Factory for quest achievements
 *
 * @author madmetzger
 */
public class SemosMonsterQuestAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_PROTECTOR = "quest.special.daily.0010";
	public static final String ID_GUARDIAN = "quest.special.daily.0050";
	public static final String ID_HERO = "quest.special.daily.0100";
	public static final String ID_CHAMPION = "quest.special.daily.0250";
	public static final String ID_VANQUISHER = "quest.special.daily.0500";


	@Override
	protected Category getCategory() {
		return Category.QUEST_SEMOS_MONSTER;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_PROTECTOR, "Semos's Protector",
			"Finish daily monster quest 10 times",
			Achievement.EASY_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("daily", 2, 9)));

		achievements.add(createAchievement(
			ID_GUARDIAN, "Semos's Guardian",
			"Finish daily monster quest 50 times",
			Achievement.EASY_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("daily", 2, 49)));

		achievements.add(createAchievement(
			ID_HERO, "Semos's Hero",
			"Finish daily monster quest 100 times",
			Achievement.MEDIUM_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("daily", 2, 99)));

		achievements.add(createAchievement(
			ID_CHAMPION, "Semos's Champion",
			"Finish daily monster quest 250 times",
			Achievement.MEDIUM_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("daily", 2, 249)));

		achievements.add(createAchievement(
			ID_VANQUISHER, "Semos's Vanquisher",
			"Finish daily monster quest 500 times",
			Achievement.HARD_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("daily", 2, 499)));

		return achievements;
	}
}
