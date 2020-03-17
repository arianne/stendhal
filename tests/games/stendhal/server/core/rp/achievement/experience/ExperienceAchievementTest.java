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
package games.stendhal.server.core.rp.achievement.experience;

import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_ADVENTURER;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_APPRENTICE;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_EXPERIENCED_ADV;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_GREENHORN;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_HIGH_MASTER;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_MASTER;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_MASTER_ADV;
import static games.stendhal.server.core.rp.achievement.factory.ExperienceAchievementFactory.ID_NOVICE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class ExperienceAchievementTest extends ZoneAndPlayerTestImpl {

	private Player player;

	private final List<String> idList = Arrays.asList(ID_GREENHORN, ID_NOVICE,
			ID_APPRENTICE, ID_ADVENTURER, ID_EXPERIENCED_ADV, ID_MASTER_ADV,
			ID_MASTER, ID_HIGH_MASTER);


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		zone = setupZone("testzone");

		super.setUp();
	}

	@Test
	public void init() {
		resetPlayer();

		doCycle(ID_GREENHORN, 10);
		doCycle(ID_NOVICE, 50);
		doCycle(ID_APPRENTICE, 100);
		doCycle(ID_ADVENTURER, 200);
		doCycle(ID_EXPERIENCED_ADV, 300);
		doCycle(ID_MASTER_ADV, 400);
		doCycle(ID_MASTER, 500);
		doCycle(ID_HIGH_MASTER, 597);
	}

	/**
	 * Resets player achievements.
	 */
	private void resetPlayer() {
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		AchievementTestHelper.init(player);
		for (final String ID: idList) {
			assertFalse(achievementReached(ID));
		}
	}

	private void doCycle(final String id, final int reqLevel) {
		while(player.getLevel() < reqLevel) {
			assertFalse(achievementReached(id));
			player.setLevel(player.getLevel() + 1);
		}
		assertTrue(achievementReached(id));
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached(final String ID) {
		return AchievementTestHelper.achievementReached(player, ID);
	}
}
