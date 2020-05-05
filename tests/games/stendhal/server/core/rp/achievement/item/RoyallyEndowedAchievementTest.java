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
package games.stendhal.server.core.rp.achievement.item;

import static games.stendhal.server.core.rp.achievement.factory.ItemAchievementFactory.ID_ROYAL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;


public class RoyallyEndowedAchievementTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();

	private Player player;

	private final String[] ITEMS_ROYAL = {
			"royal armor", "royal helmet", "royal cloak", "royal legs",
			"royal boots", "royal shield"
	};


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
		initPlayer();
		testAchievement();
	}

	private void testAchievement() {
		final int itemCount = ITEMS_ROYAL.length;
		int idx = 0;
		for (final String item: ITEMS_ROYAL) {
			idx++;

			player.incLootForItem(item, 1);
			notifier.onItemLoot(player);

			if (idx >= itemCount) {
				assertTrue(achievementReached());
			} else {
				assertFalse(achievementReached());
			}
		}
	}

	/**
	 * Resets player achievements & kills.
	 */
	private void initPlayer() {
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

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
		return AchievementTestHelper.achievementReached(player, ID_ROYAL);
	}
}
