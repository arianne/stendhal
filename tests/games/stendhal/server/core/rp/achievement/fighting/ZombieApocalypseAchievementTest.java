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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ENEMIES_ZOMBIES;
import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_ZOMBIES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Rand;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;

public class ZombieApocalypseAchievementTest extends ZoneAndPlayerTestImpl {

	private final int reqCount = 500;


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		zone = setupZone("testzone");
		AchievementTestHelper.setEnemyNames(ENEMIES_ZOMBIES);
	}

	@Test
	public void init() {
		final AchievementNotifier notifier = AchievementNotifier.get();

		for (final String enemy: ENEMIES_ZOMBIES) {
			// solo kills
			resetPlayer();
			for (int idx = 0; idx < reqCount; idx++) {
				player.setSoloKillCount(enemy, player.getSoloKill(enemy) + 1);
				notifier.onKill(player);

				if (idx < reqCount - 1) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}

			// shared kills
			resetPlayer();
			for (int idx = 0; idx < reqCount; idx++) {
				player.setSharedKillCount(enemy, player.getSharedKill(enemy) + 1);
				notifier.onKill(player);

				if (idx < reqCount - 1) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}
		}

		resetPlayer();
		int killCount = 0;
		while (killCount < reqCount) {
			final int enemyIndex = Rand.randUniform(0, ENEMIES_ZOMBIES.length - 1);
			final String enemy = ENEMIES_ZOMBIES[enemyIndex];
			final boolean soloKill = Rand.randUniform(0, 1) == 0;

			if (soloKill) {
				player.setSoloKillCount(enemy, player.getSoloKill(enemy) + 1);
			} else {
				player.setSharedKillCount(enemy, player.getSharedKill(enemy) + 1);
			}
			notifier.onKill(player);

			killCount++;
			if (killCount < reqCount) {
				assertFalse(achievementReached());
			} else {
				assertTrue(achievementReached());
			}
		}
	}

	private void resetPlayer() {
		if (player != null) {
			PlayerTestHelper.removePlayer(player.getName(), "testzone");
		}
		player = PlayerTestHelper.createPlayer("player");
		player.setPosition(0, 0);
		zone.add(player);
		assertNotNull(player);

		/*
		player.setLevel(597);
		player.setAtk(597);
		player.setDef(597);

		for (final String stat: Arrays.asList("level", "atk", "def")) {
			assertEquals(597, player.getInt(stat));
		}

		equip("chaos axe", "rhand");
		equip("mithril shield", "lhand");
		equip("mithril helmet", "head");
		equip("mithril armor", "armor");
		equip("mithril legs", "legs");
		equip("mithril boots", "feet");
		equip("mithril cloak", "cloak");
		equip("imperial ring", "finger");
		*/

		for (final String enemy: ENEMIES_ZOMBIES) {
			assertFalse(player.hasKilled(enemy));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	/*
	private void equip(final String item, final String slot) {
		PlayerTestHelper.equipWithItemToSlot(player, item, slot);
	}
	*/

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_ZOMBIES);
	}
}
