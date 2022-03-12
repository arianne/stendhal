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
import java.util.List;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.ChatCondition;


/**
 * Factory class for achievements creation with a fixed category
 *
 * @author madmetzger
 */
public abstract class AbstractAchievementFactory {

	/**
	 * @return the category the factory should use
	 */
	protected abstract Category getCategory();

	/**
	 * Creates a collection of achievements
	 *
	 * @return the achievments
	 */
	public abstract Collection<Achievement> createAchievements();

	/**
	 * Creates a single achievement
	 * @param identifier
	 * @param title
	 * @param description
	 * @param score
     * @param active
	 * @param condition
	 * @return the new Achievement
	 */
	protected Achievement createAchievement(String identifier, String title, String description, int score, boolean active, ChatCondition condition) {
		return new Achievement(identifier, title, getCategory(),  description, score, active, condition);
	}

	/**
	 * Create a list of all known achievement factories
	 * @return the list of factories
	 */
	public static List<AbstractAchievementFactory> createFactories() {
		List<AbstractAchievementFactory> list = new LinkedList<AbstractAchievementFactory>();
		//add new created factories here
		list.add(new AdosItemQuestAchievementsFactory());
		list.add(new DeathmatchAchievementFactory());
		list.add(new ExperienceAchievementFactory());
		list.add(new FightingAchievementFactory());
		list.add(new FriendAchievementFactory());
		list.add(new InteriorZoneAchievementFactory());
		list.add(new ItemAchievementFactory());
		list.add(new ObtainAchievementsFactory());
		list.add(new OutsideZoneAchievementFactory());
		list.add(new ProductionAchievementFactory());
		list.add(new QuestAchievementFactory());
		list.add(new SemosMonsterQuestAchievementFactory());
		list.add(new UndergroundZoneAchievementFactory());
		list.add(new KirdnehItemAchievementFactory());
		list.add(new MithrilbourghEnemyArmyAchievementFactory());
		list.add(new CommerceAchievementFactory());
		list.add(new KillBlordroughsAchievementFactory());
		return list;
	}
}
