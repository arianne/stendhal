/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;

import java.util.Collection;
import java.util.LinkedList;

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
		LinkedList<Achievement> achievements = new LinkedList<Achievement>();
		
		// Index where number of completions is stored
		// final int INDEX = 3;
		
		/* Disabled until ready to implement
		achievements.add(createAchievement("quest.special.weekly_army.0005", "Sergeant", "Finish Kill Enemy Army quest 5 times",
				Achievement.MEDIUM_BASE_SCORE, false,
				new QuestStateGreaterThanCondition("kill_enemy_army", INDEX, 4)));*/
		
		return achievements;
	}

}
