/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
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

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ProductionAchievementFactoryTest extends AchievementTestHelper {

	private Player player;

	private static final String[] all_seeds = {"daisies", "lilia", "pansy", "zantedeschia"};


	@Before
	public void setUp() {
		player = createPlayer("player");
		assertNotNull(player);
		init(player);
	}

	/** XXX: should we create an actual zone instead of simulating sowing? */
	@Test
	public void testSowingSeedsOfJoy() {
		final String id = "production.sow.flowers.all";
		for (final String seed: all_seeds) {
			while (player.getQuantityOfSownItems(seed) < 1000) {
				assertFalse(achievementReached(player, id));
				player.incSownForItem(seed, 1);
			}
		}
		assertTrue(achievementReached(player, id));
	}
}
