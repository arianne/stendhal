/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;


/**
 * Factory for MithrilbourghEnemyArmyAchievement
 */
public class MithrilbourghEnemyArmyAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.QUEST_MITHRILBOURGH_ENEMY_ARMY;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		// Index where number of completions is stored
		final int IDX = 3;

		achievements.add(createAchievement(
			"quest.special.kill_enemy_army.0005", "Sergeant",
			"Finish Kill Enemy Army quest 5 times",
			HOFScore.MEDIUM, true,
			new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 4)));

		achievements.add(createAchievement(
			"quest.special.kill_enemy_army.0025", "Major",
			"Finish Kill Enemy Army quest 25 times",
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 24)));

		achievements.add(createAchievement(
			"quest.special.kill_enemy_army.0050", "Major General",
			"Finish Kill Enemy Army quest 50 times",
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 49)));

		achievements.add(createAchievement(
			"quest.special.kill_enemy_army.0100", "Field Marshal",
			"Finish Kill Enemy Army quest 100 times",
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 99)));

		achievements.add(createAchievement(
			"quest.special.kill_enemy_army.0200", "Commander in Chief",
			"Finish Kill Enemy Army quest 200 times",
			HOFScore.EXTREME, true,
			new QuestStateGreaterThanCondition("kill_enemy_army", IDX, 199)));

		return achievements;
	}
}
