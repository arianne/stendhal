/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.athor.ship.CargoWorkerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class TrapsForKlaasTest extends ZonePlayerAndNPCTestImpl {
	private static final String ZONE_NAME = "testzone";

	private SpeakerNPC klaas;

	private String questName;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer(ZONE_NAME);

		final String npcName = "Klaas";
		setNpcNames(npcName);
		addZoneConfigurator(new CargoWorkerNPC(), ZONE_NAME);

		super.setUp();

		klaas = getNPC(npcName);

		// initialize quest
		final AbstractQuest quest = new TrapsForKlaas();
		questName = quest.getSlotName();
		quest.addToWorld();
	}

	@Test
	public void testQuest() {
		testInit();
		testDialogue();
	}

	private void testInit() {
		assertNotNull(player);
		assertNotNull(klaas);
		assertNotNull(questName);
	}

	private void testDialogue() {
		final Engine en = klaas.getEngine();

		assertNull(player.getQuest(questName));

		Double karma = player.getKarma();
		int xp = player.getXP();

		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Ahoy! Nice to see you in the cargo hold!", getReply(klaas));
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
				"The rats down here have been getting into the food storage. Would you help me rid us of the varmints?",
				getReply(klaas));
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Don't waste my time. I've got to protect the cargo.", getReply(klaas));
		// player loses some karma
		assertEquals(karma - 5.0, player.getKarma(), 0);

		karma = player.getKarma();

		assertEquals("rejected", player.getQuest(questName));
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
				"Thanks, I need you to bring me bring me 20 #rodent #traps. Please hurry! We can't afford to lose anymore food.",
				getReply(klaas));
		assertEquals("start", player.getQuest(questName));
		en.step(player, "rodent traps");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I don't know of anyone who sells 'em. But I did hear a story once about a fella who killed a large rat and"
				+ " discovered a trap snapped shut on its foot.",
				getReply(klaas));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Please kill some rats on your way up!", getReply(klaas));

		// player does not have any traps
		assertFalse(player.isEquipped("rodent trap"));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I could really use those #traps. How can I help you?", getReply(klaas));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// player does not have enough traps
		PlayerTestHelper.equipWithStackableItem(player, "rodent trap", 19);
		assertTrue(player.isEquipped("rodent trap"));
		assertFalse(player.isEquipped("rodent trap", 20));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_ITEM_BROUGHT, en.getCurrentState());
		assertEquals("Did you bring any traps?", getReply(klaas));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Please hurry! I just found another box of food that's been chewed through.", getReply(klaas));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_ITEM_BROUGHT, en.getCurrentState());
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I'm sorry but I need 20 #rodent #traps", getReply(klaas));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// player has enough traps
		PlayerTestHelper.equipWithStackableItem(player, "rodent trap", 1);
		assertTrue(player.isEquipped("rodent trap", 20));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_ITEM_BROUGHT, en.getCurrentState());
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		/* FIXME: need to initialize Antivenom Ring quest
		assertEquals("Thanks! I've got to get these set up as quickly as possible. Take these antidotes as a reward."
				+ " I used to know an old #apothecary. Take this note to him. Maybe he can help you out with something.",
				getReply(klaas));
		*/
		// FIXME: choosing randomly between two options?
		//assertEquals("Thanks! I've got to get these set up as quickly as possible. Take these antidotes as a reward.", getReply(klaas));
		assertEquals("done", player.getQuest(questName, 0));
		assertEquals(xp + 1000, player.getXP());
		assertEquals(karma + 10.0, player.getKarma(), 0);
		// FIXME: need to initialize Antivenom Ring quest
		//assertTrue(player.isEquippedWithInfostring("note", "note to apothecary"));

		xp = player.getXP();
		karma = player.getKarma();

		en.step(player, "apothecary");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		/* FIXME: need to initialize Antivenom Ring quest
		assertEquals(
				"I used to know an old apothecary, but don't know where he has settled down. Perhaps someone in Ados would know."
				+ " There are guards that patrol the city. They see many happenings around the area. Ask around about an"
				+ " #apothecary.",
				getReply(klaas));
		*/

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}
}
