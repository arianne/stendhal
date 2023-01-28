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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class CommerceAchievementFactoryTest extends AchievementTestHelper {

	private final Player player;


	public CommerceAchievementFactoryTest() {
		player = createPlayer("player");
	}

	@Before
	public void setUp() {
		assertNotNull(player);
		init(player);
	}

	@Test
	public void testItsHappyHourSomewhere() {
		assertTrue(achievementEnabled(CommerceAchievementFactory.ID_HAPPY_HOUR));
		for (final String drink: new String[] {"beer", "wine"}) {
			while (player.getQuantityOfBoughtItems(drink) < 100) {
				assertFalse(achievementReached(player, CommerceAchievementFactory.ID_HAPPY_HOUR));
				player.incBoughtForItem(drink, 1);
			}
		}
		assertTrue(achievementReached(player, CommerceAchievementFactory.ID_HAPPY_HOUR));
	}

	@Test
	public void testTravelingPeddler() {
		assertTrue(achievementEnabled(CommerceAchievementFactory.ID_SELL_20K));
		for (final String npcname: new String[] {"foo", "bar"}) {
			assertEquals(0, player.getCommerceTransactionAmount(npcname, true));
			while (player.getCommerceTransactionAmount(npcname, true) < 10000) {
				assertFalse(achievementReached(player, CommerceAchievementFactory.ID_SELL_20K));
				player.incCommerceTransaction(npcname, 1000, true);
			}
			assertEquals(10000, player.getCommerceTransactionAmount(npcname, true));
		}
		assertTrue(achievementReached(player, CommerceAchievementFactory.ID_SELL_20K));
	}

	@Test
	public void testCommunitySupporter() {
		assertTrue(achievementEnabled(CommerceAchievementFactory.ID_BUY_ALL));
		for (final Map.Entry<String, Integer> npc: CommerceAchievementFactory.TRADE_ALL_AMOUNTS.entrySet()) {
			assertFalse(achievementReached(player, CommerceAchievementFactory.ID_BUY_ALL));
			player.incCommerceTransaction(npc.getKey(), npc.getValue(), false);
		}
		assertTrue(achievementReached(player, CommerceAchievementFactory.ID_BUY_ALL));
	}
}
