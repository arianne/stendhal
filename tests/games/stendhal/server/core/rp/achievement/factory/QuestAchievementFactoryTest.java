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
import static utilities.QuestRunner.doQuestRestockFlowerShop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.*;
import utilities.AchievementTestHelper;
import utilities.NPCTestHelper;
import utilities.QuestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class QuestAchievementFactoryTest extends AchievementTestHelper {

	private Player player;


	@Before
	public void setUp() {
		player = createPlayer("player");
		assertNotNull(player);
		init(player);
	}

	@After
	public void tearDown() {
		QuestHelper.unloadQuests();
		assertEquals(0, QuestHelper.getLoadedSlots().size());
		// clean up NPCs
		assertTrue(NPCTestHelper.removeAllNPCs());
	}

	public void loadQuests(final IQuest... qs) {
		QuestHelper.loadQuests(qs);
		for (final IQuest q: qs) {
			assertTrue(QuestHelper.isLoaded(q));
		}
	}

	public void loadConfigurators(final ZoneConfigurator... zc) {
		ZoneAndPlayerTestImpl.setupZone("testzone", zc);
	}

	@Test
	public void testFloralFondness() {
		final int required = 50;
		final String id = "quest.flowershop.00" + required;
		// Seremela
		loadConfigurators(new games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC());
		loadQuests(new RestockFlowerShop());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			doQuestRestockFlowerShop(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("restock_flowershop", 2));
		assertTrue(achievementReached(player, id));
	}
}
