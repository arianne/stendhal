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
import static utilities.PlayerTestHelper.equipWithStackableItem;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
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
		final String id = CommerceAchievementFactory.ID_SELL_20K;
		assertTrue(achievementEnabled(id));
		final SpeakerNPC npc = new SpeakerNPC("tester");
		npc.addGreeting();
		npc.addGoodbye();
		final ShopList shops = ShopList.get();
		shops.addBuyer("buygrain", "grain", 1);
		shops.configureNPC(npc, "buygrain", false, false);
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		equipWithStackableItem(player, "grain", 20000);
		for (int idx = 0; idx < 19; idx++) {
			en.step(player, "sell 1000 grain");
			en.step(player, "yes");
		}
		en.step(player, "sell 999 grain");
		en.step(player, "yes");
		assertFalse(achievementReached(player, id));
		en.step(player, "sell grain");
		en.step(player, "yes");
		en.step(player, "bye");
		assertTrue(achievementReached(player, id));
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
