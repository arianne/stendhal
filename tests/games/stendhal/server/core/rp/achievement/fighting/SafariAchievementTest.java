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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_SAFARI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class SafariAchievementTest extends ZoneAndPlayerTestImpl {

	private final int reqTiger = 30;
	private final int reqLion = 30;
	private final int reqElephant = 50;

	private final Map<String, Integer> enemies = new HashMap<String, Integer>() {{
		put("tiger", reqTiger);
		put("lion", reqLion);
		put("elephant", reqElephant);
	}};


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
		AchievementTestHelper.setEnemyNames(enemies.keySet().toArray(new String[0]));
	}

	@Test
	public void init() {
		final int reqTotal = reqTiger + reqLion + reqElephant;

		// solo kills
		int totalKills = 0;
		resetPlayer();
		for (final String enemy: enemies.keySet()) {
			int killCount = player.getSoloKill(enemy);
			assertEquals(0, killCount);

			final int count = enemies.get(enemy);
			while (killCount < count) {
				killCount++;
				player.setSoloKillCount(enemy, killCount);
				AchievementNotifier.get().onKill(player);
			}

			totalKills += killCount;

			if (totalKills < reqTotal) {
				assertFalse(achievementReached());
			}
		}
		assertTrue(achievementReached());

		// shared kills
		totalKills = 0;
		resetPlayer();
		for (final String enemy: enemies.keySet()) {
			int killCount = player.getSharedKill(enemy);
			assertEquals(0, killCount);

			final int count = enemies.get(enemy);
			while (killCount < count) {
				killCount++;
				player.setSharedKillCount(enemy, killCount);
				AchievementNotifier.get().onKill(player);
			}

			totalKills += killCount;

			if (totalKills < reqTotal) {
				assertFalse(achievementReached());
			}
		}
		assertTrue(achievementReached());
	}

	private void resetPlayer() {
		/*
		if (player != null) {
			PlayerTestHelper.removePlayer(player.getName(), "testzone");
		}
		*/
		player = null;
		player = PlayerTestHelper.createPlayer("player");
		player.setPosition(0, 0);
		zone.add(player);
		assertNotNull(player);

		for (final String enemy: enemies.keySet()) {
			assertFalse(player.hasKilled(enemy));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_SAFARI);
	}
}
