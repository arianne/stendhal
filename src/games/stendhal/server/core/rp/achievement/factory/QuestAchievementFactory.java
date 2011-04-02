/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.maps.Region;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for quest achievements
 *
 * @author madmetzger
 */
public class QuestAchievementFactory extends AbstractAchievementFactory {

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		//daily monster quest achievements
		questAchievements.add(createAchievement("quest.special.daily.0010", "Semos' Protector", "Finish daily monster quest 10 times",
												Achievement.EASY_BASE_SCORE, true, new QuestStateGreaterThanCondition("daily", 2, 9)));
		questAchievements.add(createAchievement("quest.special.daily.0050", "Semos' Guardian", "Finish daily monster quest 50 times",
												Achievement.EASY_BASE_SCORE, true, new QuestStateGreaterThanCondition("daily", 2, 49)));
		questAchievements.add(createAchievement("quest.special.daily.0100", "Semos' Hero", "Finish daily monster quest 100 times",
												Achievement.MEDIUM_BASE_SCORE, true, new QuestStateGreaterThanCondition("daily", 2, 99)));
		questAchievements.add(createAchievement("quest.special.daily.0250", "Semos' Champion", "Finish daily monster quest 250 times",
												Achievement.MEDIUM_BASE_SCORE, true, new QuestStateGreaterThanCondition("daily", 2, 249)));
		questAchievements.add(createAchievement("quest.special.daily.0500", "Semos' Vanquisher", "Finish daily monster quest 500 times",
												Achievement.HARD_BASE_SCORE, true, new QuestStateGreaterThanCondition("daily", 2, 499)));

		//elf princess quest achievement
		questAchievements.add(createAchievement("quest.special.elf_princess.0025", "Faiumoni's Casanova", "Finish elf princess quest 25 times",
												Achievement.MEDIUM_BASE_SCORE, true, new QuestStateGreaterThanCondition("elf_princess", 2, 24)));

		//Maze
		questAchievements.add(createAchievement("quest.special.maze", "Pathfinder", "Finish the maze",
												Achievement.EASY_BASE_SCORE, true, new QuestStateGreaterThanCondition("maze", 2, 0)));

		// Ados Deathmatch
		// disabled. Currently the wrong index is being checked (it would be index 6)
		// and as per bug report https://sourceforge.net/tracker/?func=detail&aid=3148365&group_id=1111&atid=101111 the count is not saved anyway
		// questAchievements.add(createAchievement("quest.special.dm.025", "Gladiator", "Fight 25 Deathmatches",
		//		Achievement.HARD_BASE_SCORE, true, new QuestStateGreaterThanCondition("deathmatch", 1, 24)));

		// have completed all quests in Semos City?
		questAchievements.add(createAchievement("quest.special.semos", "Aide to Semos folk", "Complete all quests in Semos City",
				Achievement.MEDIUM_BASE_SCORE, true, new QuestsInRegionCompletedCondition(Region.SEMOS_CITY)));

		// have completed all quests in Ados City?
		questAchievements.add(createAchievement("quest.special.ados", "Helper of Ados city dwellers", "Complete all quests in Ados City",
				Achievement.MEDIUM_BASE_SCORE, true, new QuestsInRegionCompletedCondition(Region.ADOS_CITY)));

		return questAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.QUEST;
	}

}
