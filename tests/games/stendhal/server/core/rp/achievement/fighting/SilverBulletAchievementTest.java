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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_WEREWOLF;
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

public class SilverBulletAchievementTest {

	private static final AchievementNotifier notifier = SingletonRepository.getAchievementNotifier();
	private Player player;

	private final String enemy = "werewolf";
	private final int reqCount = 500;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		notifier.initialize();
	}

	@Before
	public void setUp() {
		AchievementTestHelper.setEnemyNames(enemy);
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
		// test with solo kills
		for (int kills = 0; kills < reqCount; kills++) {
			kill(enemy, true);

			if (kills >= reqCount - 1) {
				assertTrue(achievementReached());
			} else {
				assertFalse(achievementReached());
			}
		}

		resetPlayer();

		// test with team kills
		for (int kills = 0; kills < reqCount; kills++) {
			kill(enemy, false);

			if (kills >= reqCount - 1) {
				assertTrue(achievementReached());
			} else {
				assertFalse(achievementReached());
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

		assertFalse(player.hasKilledSolo(enemy));
		assertFalse(player.hasKilledShared(enemy));

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
		return AchievementTestHelper.achievementReached(player, ID_WEREWOLF);
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
