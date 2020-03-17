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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_POACHER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class PoacherAchievementTest extends ZoneAndPlayerTestImpl {

	// NOTE: big bad wolf & twilight slime must be excluded
	private final String[] rareEnemies = {
		"unicorn", "centaur", "pegasus", "ghost hound"
	};


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

		final List<String> enemyList = new ArrayList<String>();
		for (final String rare: rareEnemies) {
			enemyList.add(rare);
		}
		// fox is used for testing negative result
		enemyList.add("fox");
		AchievementTestHelper.setEnemyNames(enemyList);
	}

	@Test
	public void init() {
		// solo kills
		resetPlayer();
		onKill("fox");
		assertFalse(achievementReached());

		for (final String enemy: rareEnemies) {
			resetPlayer();
			onKill(enemy);
			assertTrue(achievementReached());
		}

		// shared kills
		resetPlayer();
		onKill("fox", true);
		assertFalse(achievementReached());

		for (final String enemy: rareEnemies) {
			resetPlayer();
			onKill(enemy, true);
			assertTrue(achievementReached());
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

		for (final String enemy: rareEnemies) {
			assertFalse(player.hasKilled(enemy));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private void onKill(final String enemy, final boolean shared) {
		if (shared) {
			player.setSharedKillCount(enemy, player.getSharedKill(enemy) + 1);
		} else {
			player.setSoloKillCount(enemy, player.getSoloKill(enemy) + 1);
		}

		AchievementNotifier.get().onKill(player);
	}

	private void onKill(final String enemy) {
		onKill(enemy, false);
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_POACHER);
	}
}
