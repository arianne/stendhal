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
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.atlantis.cityoutside.ZelanNPC;
import utilities.QuestHelper;


public class UnicornHornsForZelanTest extends QuestHelper {

	private SpeakerNPC zelan;
	private Player player;

	private final String slot = "unicorn_horns_for_zelan";


	@Before
	public void setUp() {
		player = createPlayer("tester");
		// load Zelan
		new ZelanNPC().configureZone(new StendhalRPZone("testzone"), null);
		zelan = SingletonRepository.getNPCList().get("Zelan");
		// load quest
		quests.loadQuest(new UnicornHornsForZelan());
	}

	@Test
	public void initTests() {
		testEntities();
		testQuest();
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(zelan);
	}

	private void testQuest() {
		assertTrue(quests.isLoaded(quests.getQuestFromSlot(slot)));

		final Engine en = zelan.getEngine();

		assertFalse(player.hasQuest(slot));
		assertEquals(0, player.getNumberOfEquipped("unicorn horn"));
		assertEquals(0, player.getNumberOfEquipped("soup"));
		assertEquals(0, player.getNumberOfEquipped("money"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		final int startXp = player.getXP();
		final double startKarma = player.getKarma();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());

		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED,
			en.getCurrentState());
		assertEquals(
			"Hello! I'm in need of some unicorn horns to make some daggers."
			+ " It is really dangerous in the woods surrounding Atlantis. If"
			+ " you are a brave sort I could really use some help gathering"
			+ " unicorn horns. Will you help me?",
			getReply(zelan));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thats ok, I will find someone else to help me.",
			getReply(zelan));
		assertEquals(startKarma - 10, player.getKarma(), 0);
		assertEquals("rejected", player.getQuest(slot, 0));

		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Great! Be careful out there lots of large monsters, and those"
				+ " centaurs are really nasty.",
			getReply(zelan));
		assertEquals("start", player.getQuest(slot, 0));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		equipWithStackableItem(player, "unicorn horn", 9);

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "done");
		assertEquals(
			"I asked you to bring me 10 unicorn horns.",
			getReply(zelan));
		en.step(player, "bye");

		equipWithStackableItem(player, "unicorn horn", 1);

		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_ITEM_BROUGHT,
			en.getCurrentState());
		assertEquals("Did you find the unicorn horns?", getReply(zelan));
		en.step(player, "yes");
		assertEquals(
			"Thanks a bunch! As a reward I will give you 3 soups and 20000"
				+ " money.",
			getReply(zelan));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		assertEquals("done", player.getQuest(slot, 0));
		assertEquals("1", player.getQuest(slot, 2));
		assertEquals(startKarma + 20, player.getKarma(), 0);
		assertEquals(startXp + 50000, player.getXP());
		assertEquals(0, player.getNumberOfEquipped("unicorn horn"));
		assertEquals(3, player.getNumberOfEquipped("soup"));
		assertEquals(20000, player.getNumberOfEquipped("money"));

		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thanks, but I don't need any more help yet.",
			getReply(zelan));
		player.setQuest(slot, 1, "0");
		en.step(player, "quest");
		assertEquals(
			"I want to make more daggers. I could really use your help again."
				+ " Would you gather more unicorn horns for me?",
			getReply(zelan));
		en.step(player, "bye");
	}
}
