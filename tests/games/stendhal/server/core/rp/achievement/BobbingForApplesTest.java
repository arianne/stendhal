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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.factory.ObtainAchievementsFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class BobbingForApplesTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;

	// ID used for achievement
	private final String achievementId = ObtainAchievementsFactory.ID_APPLES;

	// required number of apples to harves or loot
	private final int ITEM_COUNT = ObtainAchievementsFactory.COUNT_APPLES;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		notifier.initialize();
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
		setHarvestCount(999);
		assertFalse(achievementReached());
		setHarvestCount(0);
		setLootCount(999);
		assertFalse(achievementReached());
		setLootCount(0);

		assertEquals(0, player.getQuantityOfHarvestedItems("apple"));
		assertEquals(0, player.getNumberOfLootsForItem("apple"));

		setHarvestCount(1000);
		assertTrue(achievementReached());

		resetPlayer();
		setLootCount(1000);
		assertTrue(achievementReached());

		resetPlayer();
		setHarvestCount(500);
		setLootCount(499);
		assertFalse(achievementReached());

		setHarvestCount(499);
		setLootCount(500);
		assertFalse(achievementReached());

		setHarvestCount(500);
		setHarvestCount(500);
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

		assertEquals(0, player.getQuantityOfHarvestedItems("apple"));
		assertEquals(0, player.getNumberOfLootsForItem("apple"));

		assertFalse(player.arePlayerAchievementsLoaded());
		player.initReachedAchievements();
		assertTrue(player.arePlayerAchievementsLoaded());
		assertFalse(achievementReached());
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return player.hasReachedAchievement(achievementId);
	}

	private void setHarvestCount(final int count) {
		final int current = player.getQuantityOfHarvestedItems("apple");

		// XXX: are apples "harvested" or "obtained"?
		player.incHarvestedForItem("apple", count - current);
		//player.incObtainedForItem("apple", count - current);

		notifier.onObtain(player);
	}

	private void setLootCount(final int count) {
		int current = player.getNumberOfLootsForItem("apple");

		player.incLootForItem("apple", count - current);

		// FIXME: looting events do not trigger checking Category.OBTAIN achievements
		//notifier.onItemLoot(player);
		notifier.onObtain(player);
	}
}
