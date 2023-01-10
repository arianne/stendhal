/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.AchievementTestHelper.achievementReached;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC;
import games.stendhal.server.maps.quests.RestockFlowerShop;
import games.stendhal.server.util.ItemCollection;
import utilities.AchievementTestHelper;
import utilities.QuestHelper;


public class FloralFondnessTest extends QuestHelper {

	private Player player;
	private SpeakerNPC seremela;
	private String questSlot;
	private final String aId = "quest.flowershop.0050";


	@Before
	public void setUp() {
		player = createPlayer("tester");
		new FlowerGrowerNPC().configureZone(new StendhalRPZone("testzone"), null);
		seremela = SingletonRepository.getNPCList().get("Seremela");
		final RestockFlowerShop quest = new RestockFlowerShop();
		questSlot = quest.getSlotName();
		quests.loadQuest(quest);
		AchievementTestHelper.init(player);
	}

	@Test
	public void init() {
		checkEntities();
		doQuest();
	}

	private void checkEntities() {
		assertNotNull(player);
		assertFalse(player.hasQuest(questSlot));
		assertNotNull(seremela);
		assertTrue(quests.isLoaded(quests.getQuestFromSlot(questSlot)));
	}

	private void doQuest() {
		final Engine en = seremela.getEngine();
		for (int completions = 0; completions < 50; completions++) {
			assertFalse(achievementReached(player, aId));

			en.step(player, "hi");
			en.step(player, "quest");
			en.step(player, "yes");
			en.step(player, "bye");
			assertEquals("start", player.getQuest(questSlot, 0));

			final ItemCollection collection = new ItemCollection();
			collection.addFromQuestStateString(player.getQuest(questSlot), 3);

			en.step(player, "hi");
			for (final String itemName: collection.keySet()) {
				equipWithStackableItem(player, itemName, collection.get(itemName));
				en.step(player, itemName);
			}
			en.step(player, "bye");
			assertEquals("done", player.getQuest(questSlot, 0));
			assertEquals(MathHelper.parseInt(player.getQuest(questSlot, 2)), completions+1);
			// reset timer
			player.setQuest(questSlot, 1, "0");
		}
		assertTrue(achievementReached(player, aId));
	}
}
