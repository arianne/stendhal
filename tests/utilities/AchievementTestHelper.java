/***************************************************************************
 *                    Copyright © 2020-2024 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;


public abstract class AchievementTestHelper extends PlayerTestHelper {

	protected static AchievementNotifier an = AchievementNotifier.get();

	protected static final EntityManager em = SingletonRepository.getEntityManager();

	private static String[] enemyNames = null;

	private static boolean initialized = false;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	public static void init(final Player player) {
		if (!initialized) {
			// initialize the notifier
			if (!an.isInitialized()) {
				an.initialize();
			}

			if (enemyNames != null) {
				checkEnemyNames();
			}

			initialized = true;
		}

		// initialize player achievements
		assertFalse(player.arePlayerAchievementsLoaded());
		player.initReachedAchievements();
		assertTrue(player.arePlayerAchievementsLoaded());
	}

	/**
	 * Retrieves an achievement.
	 *
	 * @param id
	 *   Achievement string identifier.
	 * @return
	 *   `games.stendhal.server.core.rp.achievement.Achievement` instance or `null`.
	 */
	public static Achievement getById(final String id) {
		for (final Achievement ac: an.getAchievements()) {
			if (id.equals(ac.getIdentifier())) {
				return ac;
			}
		}
		return null;
	}

	/**
	 * Checks if an achievement is enabled.
	 *
	 * @param id
	 *   Achievement string identifier.
	 * @return
	 *   `true` if achievement is loaded & enabled.
	 */
	public static boolean achievementEnabled(final String id) {
		final Achievement ac = AchievementTestHelper.getById(id);
		if (ac != null) {
			return ac.isActive();
		}
		return false;
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	public static boolean achievementReached(final Player player, final String achievementId) {
		return player.hasReachedAchievement(achievementId);
	}

	public static void setEnemyNames(final String[] enemies) {
		enemyNames = enemies;

		if (initialized) {
			// need to re-check if name list is changed after initialization
			checkEnemyNames();
		}
	}

	public static void setEnemyNames(final List<String> enemies) {
		setEnemyNames(enemies.toArray(new String[0]));
	}

	public static void setEnemyNames(final String enemy) {
		setEnemyNames(new String[] {enemy});
	}

	public static void checkEnemyNames() {
		for (final String eName: enemyNames) {
			final Creature enemy = em.getCreature(eName);
			assertNotNull(enemy);
		}
	}
}
