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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ObtainAchievementsFactoryTest extends AchievementTestHelper {

	private Player player;


	/**
	 * Resets player achievements & kills.
	 */
	private void resetPlayer() {
		player = null;
		assertNull(player);
		player = createPlayer("player");
		assertNotNull(player);

		assertEquals(0, player.getNumberOfLootsForItem("apple"));
		assertEquals(0, player.getQuantityOfHarvestedItems("apple"));

		init(player);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
	}

	@Test
	public void testBobbingForApples() {
		final String item = "apple";
		final int reqCount = 1000;

		resetPlayer();

		player.incLootForItem(item, reqCount - 1);
		assertEquals(reqCount - 1, player.getNumberOfLootsForItem(item));
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incLootForItem(item, 1);
		assertEquals(reqCount, player.getNumberOfLootsForItem(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetPlayer();

		player.incHarvestedForItem(item, reqCount - 1);
		assertEquals(reqCount - 1, player.getQuantityOfHarvestedItems(item));
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incHarvestedForItem(item, 1);
		assertEquals(reqCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetPlayer();

		final int halfCount = reqCount / 2;

		player.incLootForItem(item, halfCount);
		player.incHarvestedForItem(item, halfCount - 1);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incHarvestedForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));

		resetPlayer();

		player.incLootForItem(item, halfCount - 1);
		player.incHarvestedForItem(item, halfCount);
		assertFalse(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
		player.incLootForItem(item, 1);
		assertEquals(halfCount, player.getNumberOfLootsForItem(item));
		assertEquals(halfCount, player.getQuantityOfHarvestedItems(item));
		assertTrue(achievementReached(player, ObtainAchievementsFactory.ID_APPLES));
	}
}
