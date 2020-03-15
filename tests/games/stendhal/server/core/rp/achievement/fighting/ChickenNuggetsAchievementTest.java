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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ENEMIES_FOWL;
import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_FOWL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;

public class ChickenNuggetsAchievementTest extends ZoneAndPlayerTestImpl {

	private final int reqCount = 100;


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
		AchievementTestHelper.setEnemyNames(ENEMIES_FOWL);
	}

	@Test
	public void init() {
		final int totalRequiredKills = reqCount * ENEMIES_FOWL.length;

		// solo kills
		resetPlayer();
		int killCount = 0;
		for (final String enemyName: ENEMIES_FOWL) {
			for (int idx = 0; idx < reqCount; idx++) {
				player.setSoloKillCount(enemyName, player.getSoloKill(enemyName) + 1);
				AchievementNotifier.get().onKill(player);
				killCount++;

				if (killCount < totalRequiredKills) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}

			assertEquals(reqCount, player.getSoloKill(enemyName));
		}

		// team kills
		resetPlayer();
		killCount = 0;
		for (final String enemyName: ENEMIES_FOWL) {
			for (int idx = 0; idx < reqCount; idx++) {
				player.setSharedKillCount(enemyName, player.getSharedKill(enemyName) + 1);
				AchievementNotifier.get().onKill(player);
				killCount++;

				if (killCount < totalRequiredKills) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}

			assertEquals(reqCount, player.getSharedKill(enemyName));
		}

		if (ENEMIES_FOWL.length > 1) {
			for (final String enemyName: ENEMIES_FOWL) {
				resetPlayer();
				for (int idx = 0; idx < totalRequiredKills; idx++) {
					player.setSoloKillCount(enemyName, player.getSoloKill(enemyName) + 1);
					AchievementNotifier.get().onKill(player);
				}

				assertFalse(achievementReached());
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

		for (final String enemy: ENEMIES_FOWL) {
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
		return AchievementTestHelper.achievementReached(player, ID_FOWL);
	}
}
