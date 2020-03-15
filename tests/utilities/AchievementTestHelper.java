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
package utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;


public abstract class AchievementTestHelper {

	protected static AchievementNotifier an = AchievementNotifier.get();

	protected static final EntityManager em = SingletonRepository.getEntityManager();

	private static String[] enemyNames = null;

	private static boolean initialized = false;


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

	private static void checkEnemyNames() {
		for (final String eName: enemyNames) {
			final Creature enemy = em.getCreature(eName);
			assertNotNull(enemy);
		}
	}
}
