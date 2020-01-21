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

public class DavidVsGoliathTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;
	private final String achievementId = "fight.giant.solo";

	private final List<String> requiredKills = Arrays.asList(FightingAchievementFactory.ENEMIES_DAVID_GOLIATH);

	// required number of solo kills for each enemy
	private final int KILL_COUNT = 20;


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
		for (final String enemy: requiredKills) {
			for (int kills = 0; kills < KILL_COUNT; kills++) {
				kill(enemy, false);
			}
			assertEquals(KILL_COUNT, player.getSharedKill(enemy));
		}
		assertFalse(achievementReached());

		for (final String enemy: requiredKills) {
			for (int kills = 0; kills < KILL_COUNT; kills++) {
				kill(enemy, true);
			}
			assertEquals(KILL_COUNT, player.getSoloKill(enemy));
		}
		assertTrue(achievementReached());

		resetPlayer();
		final int enemyCount = requiredKills.size();
		for (int idx = 0; idx < enemyCount; idx++) {
			final String enemy = requiredKills.get(idx);
			for (int kills = 0; kills < KILL_COUNT; kills++) {
				kill(enemy, true);

				if (idx >= enemyCount - 1 && kills >= KILL_COUNT - 1) {
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
