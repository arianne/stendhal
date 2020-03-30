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

import static games.stendhal.server.core.rp.achievement.factory.FightingAchievementFactory.ID_LEGEND;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class LegendAchievementTest extends ZoneAndPlayerTestImpl {

	private static EntityManager em;
	private final List<Creature> enemyList = new ArrayList<Creature>();


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		//CreatureTestHelper.generateRPClasses();
		em = SingletonRepository.getEntityManager();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		zone = setupZone("testzone");
		super.setUp();

		initEnemies();
	}

	@Test
	public void init() {
		assertNotEquals(0, enemyList.size());

		resetPlayer();

		for (final Creature enemy: enemyList) {
			if (!enemy.isAbnormal()) {
				assertFalse(achievementReached());
				onKill(enemy.getName());
			}
		}

		assertTrue(achievementReached());
	}

	private void resetPlayer() {
		if (player != null) {
			PlayerTestHelper.removePlayer(player.getName(), "testzone");
		}
		player = PlayerTestHelper.createPlayer("player");
		player.setPosition(0, 0);
		zone.add(player);
		assertNotNull(player);

		for (final Creature cr: enemyList) {
			assertFalse(player.hasKilled(cr.getName()));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private void initEnemies() {
		for (final DefaultCreature cr: em.getDefaultCreatures()) {
			enemyList.add(em.getCreature(cr.getCreatureName()));
		}
	}

	private void onKill(final String enemy) {
		player.setSoloKillCount(enemy, player.getSoloKill(enemy) + 1);
		AchievementNotifier.get().onKill(player);
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_LEGEND);
	}
}
