/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.achievement.factory.ObtainAchievementsFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;


public class BobbingForApplesTest {

	private Player player;

	// ID used for achievement
	private final String achievementId = ObtainAchievementsFactory.ID_APPLES;

	// required number of apples to harves or loot
	private final int itemCount = ObtainAchievementsFactory.COUNT_APPLES;
	private final String item = "apple";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void init() {
		resetPlayer();
		testAchievement();
	}

	private void testAchievement() {
		player.incLootForItem(item, itemCount - 1);
		assertEquals(itemCount - 1, player.getNumberOfLootsForItem(item));
		assertFalse(achievementReached());
		player.incLootForItem(item, 1);
		assertEquals(itemCount, player.getNumberOfLootsForItem(item));
		assertTrue(achievementReached());

		resetPlayer();

		player.incHarvestedForItem(item, itemCount - 1);
		assertEquals(itemCount - 1, player.getQuantityOfHarvestedItems(item));
		assertFalse(achievementReached());
		player.incHarvestedForItem(item, 1);
		assertEquals(itemCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached());

		resetPlayer();

		final int halfCount = itemCount / 2;

		player.incLootForItem(item, halfCount);
		player.incHarvestedForItem(item, halfCount - 1);
		assertFalse(achievementReached());
		player.incHarvestedForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached());

		resetPlayer();

		player.incLootForItem(item, halfCount - 1);
		player.incHarvestedForItem(item, halfCount);
		assertFalse(achievementReached());
		player.incLootForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached());
	}


	/**
	 * Resets player achievements & kills.
	 */
	private void resetPlayer() {
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		assertEquals(0, player.getNumberOfLootsForItem(item));
		assertEquals(0, player.getQuantityOfHarvestedItems(item));

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, achievementId);
	}
}
