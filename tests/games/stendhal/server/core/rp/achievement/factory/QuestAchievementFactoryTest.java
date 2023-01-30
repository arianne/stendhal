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
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;
import static utilities.ZoneAndPlayerTestImpl.setupZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.*;
import utilities.AchievementTestHelper;
import utilities.NPCTestHelper;
import utilities.QuestHelper;
import utilities.QuestRunner;


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

	private void loadQuests(final IQuest... qs) {
		QuestHelper.loadQuests(qs);
		for (final IQuest q: qs) {
			assertTrue(QuestHelper.isLoaded(q));
		}
	}

	private void setupZones(final String... zones) {
		for (final String zone: zones) {
			setupZone(zone);
		}
	}

	private void loadConfigurators(final ZoneConfigurator... zc) {
		setupZone("testzone", zc);
	}

	@Test
	public void testFaiumonisCasanova() {
		final int required = 25;
		final String id = "quest.special.elf_princess.00" + required;
		setupZones("int_semos_house");
		loadConfigurators(
			new games.stendhal.server.maps.nalwor.tower.PrincessNPC(),
			new games.stendhal.server.maps.semos.house.FlowerSellerNPC()
		);
		assertNotNull(getSpeakerNPC("Tywysoga"));
		assertNotNull(getSpeakerNPC("Rose Leigh"));
		loadQuests(new ElfPrincess());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestElfPrincess(player);
		}
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFloralFondness() {
		final int required = 50;
		final String id = "quest.flowershop.00" + required;
		loadConfigurators(new games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC());
		assertNotNull(getSpeakerNPC("Seremela"));
		loadQuests(new RestockFlowerShop());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestRestockFlowerShop(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("restock_flowershop", 2));
		assertTrue(achievementReached(player, id));
	}
}
