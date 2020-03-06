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
import static org.junit.Assert.assertTrue;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;


public abstract class AchievementTestHelper {

	public static void init(final Player player) {
		// initialize the notifier
		SingletonRepository.getAchievementNotifier().initialize();

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
}
