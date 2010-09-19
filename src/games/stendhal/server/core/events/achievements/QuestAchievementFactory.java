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
package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for quest achievements
 *  
 * @author madmetzger
 */
public class QuestAchievementFactory extends AchievementFactory {
	
	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		//daily monster quest achievements
		questAchievements.add(createAchievement("quest.special.daily.0010", "Semos' Protector", "Finish daily monster quest 10 times",
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 9)));
		questAchievements.add(createAchievement("quest.special.daily.0050", "Semos' Guardian", "Finish daily monster quest 50 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 49)));
		questAchievements.add(createAchievement("quest.special.daily.0100", "Semos' Hero", "Finish daily monster quest 100 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 99)));
		questAchievements.add(createAchievement("quest.special.daily.0250", "Semos' Champion", "Finish daily monster quest 250 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 249)));
		questAchievements.add(createAchievement("quest.special.daily.0500", "Semos' Vanquisher", "Finish daily monster quest 500 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 499)));
		//daily item quest achievements
		questAchievements.add(createAchievement("quest.special.daily_item.0010", "Ados' Supporter", "Finish daily item quest 10 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 9)));
		questAchievements.add(createAchievement("quest.special.daily_item.0050", "Ados' Provider", "Finish daily item quest 50 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 49)));
		questAchievements.add(createAchievement("quest.special.daily_item.0100", "Ados' Supplier", "Finish daily item quest 100 times",
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 99)));
		questAchievements.add(createAchievement("quest.special.daily_item.0250", "Ados' Stockpiler", "Finish daily item quest 250 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 249)));
		questAchievements.add(createAchievement("quest.special.daily_item.0500", "Ados' Hoarder", "Finish daily item quest 500 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 499)));
		//weekly item quest achievement
		questAchievements.add(createAchievement("quest.special.weekly_item.0005", "Archaeologist", "Finish weekly item quest 5 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		//elf princess quest achievement
		questAchievements.add(createAchievement("quest.special.elf_princess.0025", "Faiumoni's Casanova", "Finish elf princess quest 25 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("elf_princess", 2, 24)));
		//ultimate collector quest achievement
		questAchievements.add(createAchievement("quest.special.collector", "Ultimate Collector", "Finish ultimate collector quest", 
												Achievement.HARD_BASE_SCORE, new QuestCompletedCondition("ultimate_collector")));
		return questAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.QUEST;
	}

}
