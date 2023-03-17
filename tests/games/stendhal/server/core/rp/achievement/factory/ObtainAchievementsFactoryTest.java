/***************************************************************************
 *                    Copyright © 2020-2023 - Arianne                      *
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ObtainAchievementsFactoryTest extends AchievementTestHelper {

	private static final EntityManager entities = SingletonRepository.getEntityManager();

	private Player player;

	private final String[] allVeggie = {
		"carrot", "salad", "broccoli", "cauliflower", "leek", "onion",
		"courgette", "spinach", "collard", "garlic", "artichoke"
	};
	private final String[] allFruit = {
		"apple", "banana", "cherry", "coconut", "grapes", "olive", "pear",
		"pineapple", "pomegranate", "tomato", "watermelon"
	};
	private final String[] allFish = {
		"char", "clownfish", "cod", "mackerel", "perch", "red lionfish",
		"roach", "surgeonfish", "trout"
	};
	private final String[] allFlower = {
		"daisies", "lilia", "pansy", "zantedeschia"
	};
	private final String[] allHerb = {
		"arandula", "kekik", "mandragora", "sclaria"
	};


	@Before
	public void setUp() {
		player = createPlayer("player");
		assertNotNull(player);
		init(player);
	}

	private void runHarvestTest(final String id, final String[] items, final int requiredAmount) {
		assertTrue(achievementEnabled(id));
		for (final String name: items) {
			assertNotNull(entities.getItem(name));
			assertEquals(0, player.getQuantityOfHarvestedItems(name));
			for (int idx = 0; idx < requiredAmount; idx++) {
				assertFalse(achievementReached(player, id));
				player.incHarvestedForItem(name, 1);
				assertEquals(idx + 1, player.getQuantityOfHarvestedItems(name));
			}
		}
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFarmer() {
		runHarvestTest("obtain.harvest.vegetable", allVeggie, 3);
	}

	@Test
	public void testFruitSalad() {
		runHarvestTest("obtain.harvest.fruit", allFruit, 3);
	}

	@Test
	public void testFisherman() {
		runHarvestTest("obtain.fish", allFish, 15);
	}

	@Test
	public void testGreenThumb() {
		runHarvestTest("obtain.harvest.flower", allFlower, 20);
	}

	@Test
	public void testHerbalPractitioner() {
		runHarvestTest("obtain.harvest.herb", allHerb, 20);
	}

	/**
	 * Resets player achievements & kills.
	 */
	private void resetApples() {
		player = null;
		assertNull(player);
		player = createPlayer("player");
		assertNotNull(player);

		assertEquals(0, player.getNumberOfLootsForItem("apple"));
		assertEquals(0, player.getQuantityOfHarvestedItems("apple"));

		init(player);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
	}

	@Test
	public void testBobbingForApples() {
		assertTrue(achievementEnabled(ObtainAchievementsFactory.ID_APPLES));

		final String item = "apple";
		final int reqCount = 1000;

		resetApples();

		player.incLootForItem(item, reqCount - 1);
		assertEquals(reqCount - 1, player.getNumberOfLootsForItem(item));
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incLootForItem(item, 1);
		assertEquals(reqCount, player.getNumberOfLootsForItem(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetApples();

		player.incHarvestedForItem(item, reqCount - 1);
		assertEquals(reqCount - 1, player.getQuantityOfHarvestedItems(item));
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incHarvestedForItem(item, 1);
		assertEquals(reqCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetApples();

		final int halfCount = reqCount / 2;

		player.incLootForItem(item, halfCount);
		player.incHarvestedForItem(item, halfCount - 1);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incHarvestedForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetApples();

		player.incLootForItem(item, halfCount - 1);
		player.incHarvestedForItem(item, halfCount);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incLootForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
	}
}
