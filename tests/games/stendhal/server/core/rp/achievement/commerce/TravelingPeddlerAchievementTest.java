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
package games.stendhal.server.core.rp.achievement.commerce;

import static games.stendhal.server.core.rp.achievement.factory.CommerceAchievementFactory.ID_SELL_20K;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.PlayerTestHelper.createPlayer;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class TravelingPeddlerAchievementTest extends AchievementTestHelper {

	@Test
	public void initTest() {
		final Player player = createPlayer("tester");
		assertNotNull(player);
		init(player);
		assertTrue(achievementEnabled(ID_SELL_20K));

		for (final String npcname: new String[] {"foo", "bar"}) {
			assertEquals(0, player.getCommerceTransactionAmount(npcname, true));
			while (player.getCommerceTransactionAmount(npcname, true) < 10000) {
				assertFalse(achievementReached(player, ID_SELL_20K));
				player.incCommerceTransaction(npcname, 1000, true);
			}
			assertEquals(10000, player.getCommerceTransactionAmount(npcname, true));
		}
		assertTrue(achievementReached(player, ID_SELL_20K));
	}
}
