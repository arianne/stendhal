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
package games.stendhal.server.core.rp.achievement.fighting;

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ENEMIES_GIANTS;
import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_GIANTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;

public class DavidVsGoliathAchievementTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;
	private final int reqCount = 20;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		notifier.initialize();
	}

	@Before
	public void setUp() {
		AchievementTestHelper.setEnemyNames(ENEMIES_GIANTS);
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
		for (final String enemy: ENEMIES_GIANTS) {
			for (int kills = 0; kills < reqCount; kills++) {
				kill(enemy, false);
			}
			assertEquals(reqCount, player.getSharedKill(enemy));
		}
		assertFalse(achievementReached());

		for (final String enemy: ENEMIES_GIANTS) {
			for (int kills = 0; kills < reqCount; kills++) {
				kill(enemy, true);
			}
			assertEquals(reqCount, player.getSoloKill(enemy));
		}
		assertTrue(achievementReached());

		resetPlayer();
		final int enemyCount = ENEMIES_GIANTS.length;
		for (int idx = 0; idx < enemyCount; idx++) {
			final String enemy = ENEMIES_GIANTS[idx];
			for (int kills = 0; kills < reqCount; kills++) {
				kill(enemy, true);

				if (idx >= enemyCount - 1 && kills >= reqCount - 1) {
					assertTrue(achievementReached());
				} else {
					assertFalse(achievementReached());
				}
			}
		}
	}


	/**
	 * Resets player achievements & kills.
	 */
	private void resetPlayer() {
		//PlayerTestHelper.removePlayer(player); // IllegalArgumentException
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		for (final String enemy: ENEMIES_GIANTS) {
			assertFalse(player.hasKilledSolo(enemy));
			assertFalse(player.hasKilledShared(enemy));
		}

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
		return AchievementTestHelper.achievementReached(player, ID_GIANTS);
	}

	/**
	 * Increments kill count for enemy.
	 *
	 * @param enemyName
	 * 		Name of enemy to kill.
	 * @param solo
	 * 		If <code>true</code>, player was not assisted in kill.
	 */
	private void kill(final String enemyName, final boolean solo) {
		if (solo) {
			player.setSoloKillCount(enemyName, player.getSoloKill(enemyName) + 1);
		} else {
			player.setSharedKillCount(enemyName, player.getSharedKill(enemyName) + 1);
		}

		notifier.onKill(player);
	}
}
