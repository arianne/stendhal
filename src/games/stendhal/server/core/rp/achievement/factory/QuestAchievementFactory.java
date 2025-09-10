/* $Id$ */
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
import games.stendhal.server.core.rp.achievement.condition.QuestCountCompletedCondition;
import games.stendhal.server.core.rp.achievement.condition.QuestsInRegionCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.maps.Region;


/**
 * Factory for quest achievements
 *
 * @author madmetzger
 */
public class QuestAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_FLOWERSHOP = "quest.flowershop.0050";


	@Override
	protected Category getCategory() {
		return Category.QUEST;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		// Elf Princess quest achievement
		achievements.add(createAchievement(
			"quest.special.elf_princess.0025", "Faiumoni's Casanova",
			"Finish elf princess quest 25 times",
			HOFScore.MEDIUM, true,
			new QuestStateGreaterThanCondition("elf_princess", 2, 24)));

		// Kill Monks quest achievement
		achievements.add(createAchievement(
			"quest.special.kill_monks.0025", "Heretic",
			"Finish Kill Monks quest 25 times",
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("kill_monks", 2, 24)));

		// Maze
		achievements.add(createAchievement(
			"quest.special.maze", "Pathfinder",
			"Finish the maze",
			HOFScore.EASY, true,
			new QuestStateGreaterThanCondition("maze", 2, 0)));

		// Balloon for Bobby
		achievements.add(createAchievement(
			"quest.bobby.balloons.0005", "Fairgoer",
			"Bring Bobby 5 balloons",
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("balloon_bobby", 1, 4)));

		// Meal for Groongo Rahnnt
		achievements.add(createAchievement(
			"quest.groongo.meals.0050", "Patiently Waiting on Grumpy",
			"Serve up 50 decent meals to Groongo Rahnnt",
			HOFScore.MEDIUM, true,
			new QuestStateGreaterThanCondition("meal_for_groongo", 7, 49)));

		// Restock the Flower Shop
		achievements.add(createAchievement(
			ID_FLOWERSHOP, "Floral Fondness",
			"Help restock Nalwor flower shop 50 times",
			HOFScore.MEDIUM, true,
			new QuestStateGreaterThanCondition("restock_flowershop", 2, 49)));

		// have completed all quests in Semos City?
		achievements.add(createAchievement(
			"quest.special.semos", "Aide to Semos Folk",
			"Complete all quests in Semos City",
			HOFScore.MEDIUM, true,
			new QuestsInRegionCompletedCondition(Region.SEMOS_CITY)));

		// have completed all quests in Ados City?
		achievements.add(createAchievement(
			"quest.special.ados", "Helper of Ados City Dwellers",
			"Complete all quests in Ados City",
			HOFScore.MEDIUM, true,
			new QuestsInRegionCompletedCondition(Region.ADOS_CITY)));

		// complete nearly all the quests in the game?
		achievements.add(createAchievement(
			"quest.count.80", "Quest Junkie",
			"Complete at least 80 quests",
			HOFScore.MEDIUM, true,
			new QuestCountCompletedCondition(80)));

		// deliver 5 hot pizzas
		achievements.add(createAchievement(
			"quest.pizza_delivery.hot.0025", "30 Minutes or Less",
			"Deliver 25 hot pizzas",
			HOFScore.EASY, true,
			new QuestStateGreaterThanCondition("pizza_delivery", 3, 24)));

		return achievements;
	}
}
