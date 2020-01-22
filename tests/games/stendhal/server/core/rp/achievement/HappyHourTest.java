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

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.factory.CommerceAchievementFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class HappyHourTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;

	// items used in achievement
	private final List<String> itemList = Arrays.asList(CommerceAchievementFactory.ITEMS_HAPPY_HOUR);

	// ID used for achievement
	private final String achievementId = CommerceAchievementFactory.ID_HAPPY_HOUR;

	// required number of apples to harves or loot
	private final int ITEM_COUNT = CommerceAchievementFactory.COUNT_HAPPY_HOUR;


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
		setBoughtCount("beer", 0);
		assertFalse(achievementReached());
		setBoughtCount("wine", 0);
		assertFalse(achievementReached());

		setBoughtCount("beer", ITEM_COUNT);
		setBoughtCount("wine", ITEM_COUNT - 1);
		assertFalse(achievementReached());

		setBoughtCount("beer", ITEM_COUNT - 1);
		setBoughtCount("wine", ITEM_COUNT);
		assertFalse(achievementReached());

		setBoughtCount("beer", ITEM_COUNT);
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

		for (final String item: itemList) {
			assertEquals(0, player.getQuantityOfBoughtItems(item));
		}

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

	/**
	 * Sets the number of items purchased by player.
	 *
	 * @param item
	 * 		Name of item.
	 * @param count
	 * 		New value to set.
	 */
	private void setBoughtCount(final String item, final int count) {
		final int current = player.getQuantityOfBoughtItems(item);

		player.incBoughtForItem(item, count - current);
		notifier.onObtain(player);
	}
}
