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

import static games.stendhal.server.core.rp.achievement.factory.CommerceAchievementFactory.ID_BUY_ALL;
import static games.stendhal.server.core.rp.achievement.factory.CommerceAchievementFactory.TRADE_ALL_AMOUNTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.PlayerTestHelper.createPlayer;

import java.util.Map;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;


public class CommunitySupporterAchievementTest extends AchievementTestHelper {

	private final Player player;


	public CommunitySupporterAchievementTest() {
		player = createPlayer("player");
	}

	@Test
	public void init() {
		assertNotNull(player);
		init(player);
		assertTrue(achievementEnabled(ID_BUY_ALL));

		for (final Map.Entry<String, Integer> npc: TRADE_ALL_AMOUNTS.entrySet()) {
			assertFalse(achievementReached(player, ID_BUY_ALL));
			player.incCommerceTransaction(npc.getKey(), npc.getValue(), false);
		}
		assertTrue(achievementReached(player, ID_BUY_ALL));
	}
}
