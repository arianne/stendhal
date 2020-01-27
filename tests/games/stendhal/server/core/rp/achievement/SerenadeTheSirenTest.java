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
import games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class SerenadeTheSirenTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;

	// ID used for achievement
	private final String achievementId = FightingAchievementFactory.ID_MERMAIDS;

	// list of enemies
	private final List<String> requiredKills = Arrays.asList(FightingAchievementFactory.ENEMIES_MERMAIDS);

	// required number of kills for each enemy
	private final int KILL_COUNT = FightingAchievementFactory.COUNT_MERMAIDS;


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
		// test with solo kills for each enemy
		for (final String enemy: requiredKills) {
			for (int kills = 0; kills < KILL_COUNT; kills++) {
				kill(enemy, true);

				if (kills >= KILL_COUNT - 1) {
					assertTrue(achievementReached());
				} else {
					assertFalse(achievementReached());
				}
			}
			assertEquals(KILL_COUNT, player.getSoloKill(enemy));

			resetPlayer();
		}

		// test with team kills for each enemy
		for (final String enemy: requiredKills) {
			for (int kills = 0; kills < KILL_COUNT; kills++) {
				kill(enemy, false);

				if (kills >= KILL_COUNT - 1) {
					assertTrue(achievementReached());
				} else {
					assertFalse(achievementReached());
				}
			}
			assertEquals(KILL_COUNT, player.getSharedKill(enemy));

			resetPlayer();
		}

		// test with mixed kills
		final int enemyTypes = requiredKills.size();
		final double killsPerType = KILL_COUNT / enemyTypes / 2; // solo & team kills

		for (int eType = 0; eType < enemyTypes; eType++) {
			for (int kill = 0; kill < killsPerType; kill++) {
				final String enemy = requiredKills.get(eType);

				kill(enemy, true);
				kill(enemy, false);

				if (enemy.equals(requiredKills.get(enemyTypes - 1)) && kill == killsPerType - 1) {
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
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		for (final String enemy: requiredKills) {
			assertFalse(player.hasKilledSolo(enemy));
			assertFalse(player.hasKilledShared(enemy));
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
