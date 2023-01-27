/***************************************************************************
 *                    Copyright © 2020-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.PlayerTestHelper.createPlayer;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ExperienceAchievementFactoryTest extends AchievementTestHelper {

	private final Player player;


	public ExperienceAchievementFactoryTest() {
		player = createPlayer("player");
	}

	@Before
	public void setUp() throws Exception {
		assertNotNull(player);
		init(player);
	}

	@Test
	public void initTests() {
		doCycle(ExperienceAchievementFactory.ID_GREENHORN, 10);
		doCycle(ExperienceAchievementFactory.ID_NOVICE, 50);
		doCycle(ExperienceAchievementFactory.ID_APPRENTICE, 100);
		doCycle(ExperienceAchievementFactory.ID_ADVENTURER, 200);
		doCycle(ExperienceAchievementFactory.ID_EXPERIENCED_ADV, 300);
		doCycle(ExperienceAchievementFactory.ID_MASTER_ADV, 400);
		doCycle(ExperienceAchievementFactory.ID_MASTER, 500);
		doCycle(ExperienceAchievementFactory.ID_HIGH_MASTER, 597);
	}

	private void doCycle(final String id, final int reqLevel) {
		while(player.getLevel() < reqLevel) {
			assertFalse(achievementReached(player, id));
			player.setLevel(player.getLevel() + 1);
		}
		assertTrue(achievementReached(player, id));
	}
}
