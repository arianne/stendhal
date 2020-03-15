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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.COUNT_PACHYDERM;
import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ENEMIES_PACHYDERM;
import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_PACHYDERM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;

public class PachydermMayhemTest extends ZoneAndPlayerTestImpl {

	private static final EntityManager entityManager = SingletonRepository.getEntityManager();

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
	}

	@Test
	public void init() {
		resetPlayer();

		testEnemies();
		testAchievement();
	}

	private void testEnemies() {
		for (final String enemyName: ENEMIES_PACHYDERM) {
			// check entity is valid
			final Creature enemy = createEnemy(enemyName);
			zone.remove(enemy);
		}
	}

	private void testAchievement() {
		final int totalRequiredKills = COUNT_PACHYDERM * ENEMIES_PACHYDERM.length;

		// solo kills
		int killCount = 0;
		for (final String enemyName: ENEMIES_PACHYDERM) {
			for (int idx = 0; idx < COUNT_PACHYDERM; idx++) {
				player.setSoloKillCount(enemyName, player.getSoloKill(enemyName) + 1);
				AchievementNotifier.get().onKill(player);

				/* FIXME: how to initialize RPManager
				Creature enemy = createEnemy(enemyName);
				player.setTarget(enemy);
				assertTrue(player.getAttackTarget().equals(enemy));

				while (enemy.getHP() > 0) {
					//player.attack();
					StendhalRPAction.playerAttack(player, enemy);
				}

				assertFalse(enemy.nextTo(player));
				*/

				killCount++;

				if (killCount < totalRequiredKills) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}

			assertEquals(COUNT_PACHYDERM, player.getSoloKill(enemyName));
		}

		// team kills
		resetPlayer();
		killCount = 0;
		for (final String enemyName: ENEMIES_PACHYDERM) {
			for (int idx = 0; idx < COUNT_PACHYDERM; idx++) {
				player.setSharedKillCount(enemyName, player.getSharedKill(enemyName) + 1);
				AchievementNotifier.get().onKill(player);
				killCount++;

				if (killCount < totalRequiredKills) {
					assertFalse(achievementReached());
				} else {
					assertTrue(achievementReached());
				}
			}

			assertEquals(COUNT_PACHYDERM, player.getSharedKill(enemyName));
		}

		if (ENEMIES_PACHYDERM.length > 1) {
			for (final String enemyName: ENEMIES_PACHYDERM) {
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

		for (final String enemy: ENEMIES_PACHYDERM) {
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

	private Creature createEnemy(final String enemyName) {
		final Creature enemy = entityManager.getCreature(enemyName);
		assertNotNull(enemy);
		enemy.setPosition(player.getX() + 1, player.getY());
		zone.add(enemy);

		assertTrue(enemy.nextTo(player));

		return enemy;
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_PACHYDERM);
	}
}
