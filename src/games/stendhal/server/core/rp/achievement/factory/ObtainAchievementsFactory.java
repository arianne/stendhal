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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.PlayerGotNumberOfItemsFromWellCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasHarvestedNumberOfItemsCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.player.Player;


/**
 * factory for obtaining items related achievements.
 *
 * @author madmetzger
 */
public class ObtainAchievementsFactory extends AbstractAchievementFactory {

	public static final String ID_APPLES = "obtain.apple";


	@Override
	protected Category getCategory() {
		return Category.OBTAIN;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		// Wishing well achievement
		achievements.add(createAchievement(
			"obtain.wish", "A Wish Came True",
			"Get an item from the wishing well",
			HOFScore.EASY, true,
			new PlayerGotNumberOfItemsFromWellCondition(0)));

		// Vegetable harvest achievement
		achievements.add(createAchievement(
			"obtain.harvest.vegetable", "Farmer",
			"Harvest 3 of all vegetables that grow in Faiumoni",
			HOFScore.EASY, true,
			new PlayerHasHarvestedNumberOfItemsCondition(3,
				"carrot", "lettuce", "broccoli", "cauliflower", "leek",
				"onion", "zucchini", "spinach", "collard", "garlic", "artichoke")));

		// fruit harvest achievement
		achievements.add(createAchievement(
			"obtain.harvest.fruit", "Fruit Salad",
			"Harvest 3 of all fruit that grow in Faiumoni",
			HOFScore.EASY, true,
			new PlayerHasHarvestedNumberOfItemsCondition(3,
				"apple", "banana", "cherry", "coconut", "grapes", "olive", "pear",
				"pineapple", "pomegranate", "tomato", "watermelon")));

		// Fishing achievement
		achievements.add(createAchievement(
			"obtain.fish", "Fisherman",
			"Catch 15 of each type of fish",
			HOFScore.MEDIUM, true,
			new PlayerHasHarvestedNumberOfItemsCondition(15,
				"char", "clownfish", "cod", "mackerel", "perch",
				"red lionfish", "roach", "surgeonfish", "trout")));

		// ultimate collector quest achievement
		achievements.add(createAchievement(
			"quest.special.collector", "Ultimate Collector",
			"Finish ultimate collector quest",
			HOFScore.HARD, true,
			new QuestCompletedCondition("ultimate_collector")));

		// flower harvest
		achievements.add(createAchievement(
			"obtain.harvest.flower", "Green Thumb",
			"Harvest 20 of each type of growable flower",
			HOFScore.EASY, true,
			new PlayerHasHarvestedNumberOfItemsCondition(20,
				"daisies", "lilia", "pansy", "zantedeschia")));

		// herb harvest
		achievements.add(createAchievement(
			"obtain.harvest.herb", "Herbal Practitioner",
			"Harvest 20 of each type of herb found growing in Faiumoni",
			HOFScore.EASY, true,
			new PlayerHasHarvestedNumberOfItemsCondition(20,
				"arandula", "kekik", "mandragora", "sclaria")));

		// loot or harvest apples
		achievements.add(createAchievement(
				ID_APPLES, "Bobbing for Apples",
				"Harvest or loot 1,000 apples",
				HOFScore.EASY, true,
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						final int harvested = player.getQuantityOfHarvestedItems("apple");
						final int looted = player.getNumberOfLootsForItem("apple");

						return harvested + looted >= 1000;
					}
				}));

		// find the lucky four-leaf clover
		achievements.add(createAchievement(
			"obtain.clover", "Luck O' the Irish",
			"Find the lucky four-leaf clover",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasHarvestedNumberOfItemsCondition(1, "four-leaf clover")));

		return achievements;
	}
}
