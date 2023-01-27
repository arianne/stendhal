/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
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

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class ItemAchievementFactoryTest extends AchievementTestHelper {

	private final Player player;

	private final String[] items_royal = {
		"royal armor", "royal helmet", "royal cloak",
		"royal legs", "royal boots", "royal shield"
	};
	private final String[] items_magic = {
		"magic plate armor", "magic chain helmet", "magic plate legs",
		"magic plate boots", "magic cloak", "magic plate shield"
	};


	public ItemAchievementFactoryTest() {
		player = createPlayer("player");
	}

	@Before
	public void setUp() {
		assertNotNull(player);
		init(player);
	}

	@Test
	public void testRoyallyEndowed() {
		assertTrue(achievementEnabled(ItemAchievementFactory.ID_ROYAL));
		for (final String item: items_royal) {
			assertFalse(achievementReached(player, ItemAchievementFactory.ID_ROYAL));
			player.incLootForItem(item, 1);
		}
		assertTrue(achievementReached(player, ItemAchievementFactory.ID_ROYAL));
	}

	@Test
	public void testMagicSupplies() {
		assertTrue(achievementEnabled(ItemAchievementFactory.ID_MAGIC));
		for (final String item: items_magic) {
			assertFalse(achievementReached(player, ItemAchievementFactory.ID_MAGIC));
			player.incLootForItem(item, 1);
		}
		assertTrue(achievementReached(player, ItemAchievementFactory.ID_MAGIC));
	}
}
