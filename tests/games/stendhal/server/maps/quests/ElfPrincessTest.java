/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.nalwor.tower.PrincessNPC;
import games.stendhal.server.maps.semos.house.FlowerSellerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class ElfPrincessTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private SpeakerNPC npcRose = null;
	private Engine enRose = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		ZoneConfigurator zoneConf = new PrincessNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Tywysoga");
		en = npc.getEngine();

		final StendhalRPZone zone = new StendhalRPZone("int_semos_house");
		MockStendlRPWorld.get().addRPZone(zone);
		zoneConf = new FlowerSellerNPC();
		zoneConf.configureZone(zone, null);
		npcRose = SingletonRepository.getNPCList().get("Rose Leigh");
		enRose = npcRose.getEngine();

		final AbstractQuest quest = new ElfPrincess();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Hail to thee, human.", getReply(npc));
		en.step(player, "help");
		assertEquals("A persistent person could do a #task for me.", getReply(npc));
		en.step(player, "task");
		assertEquals("Will you find the wandering flower seller, Rose Leigh, and get from her my favourite flower, the Rhosyd?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh, never mind. Bye then.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hail to thee, human.", getReply(npc));
		en.step(player, "task");
		assertEquals("Will you find the wandering flower seller, Rose Leigh, and get from her my favourite flower, the Rhosyd?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! Once you find it, say #flower to me so I know you have it. I'll be sure to give you a nice reward.", getReply(npc));
		en.step(player, "flower");
		assertEquals("You don't seem to have a rhosyd bloom with you. But Rose Leigh wanders all over the island, I'm sure you'll find her one day!", getReply(npc));
		en.step(player, "task");
		assertEquals("I do so love those pretty flowers from Rose Leigh ...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, strange one.", getReply(npc));

		// -----------------------------------------------
		// Find Rose Leigh and get the flower from her

		String roseSays = getReply(npcRose);
		assertTrue("Flowers! Get your fresh flowers here!".equals(roseSays) || (roseSays == null));
		enRose.step(player, "hi");
		assertEquals("Hello dearie. My far sight tells me you need a pretty flower for some fair maiden. Here ye arr, bye now.", getReply(npcRose));
		assertEquals(ConversationStates.IDLE, enRose.getCurrentState());


		// -----------------------------------------------
		// return to Tywysoga

		en.step(player, "hi");
		assertEquals("Hail to thee, human.", getReply(npc));
		en.step(player, "task");
		assertEquals("I do so love those pretty flowers from Rose Leigh ...", getReply(npc));
		en.step(player, "flower");
		assertTrue(getReply(npc).startsWith("Thank you! Take these "));
		assertTrue(player.isEquipped("gold bar"));
		// [00:09] superkym earns 5000 experience points.
		en.step(player, "bye");
		assertEquals("Goodbye, strange one.", getReply(npc));

		// -----------------------------------------------
		// talk to Rose Leigh without having an active task to fetch flowser

		enRose.step(player, "hi");
		assertEquals("I've got nothing for you today, sorry dearie. I'll be on my way now, bye.", getReply(npcRose));

		// -----------------------------------------------
		// do the quest again

		en.step(player, "hi");
		assertEquals("Hail to thee, human.", getReply(npc));
		en.step(player, "task");
		assertEquals("The last Rhosyd you brought me was so lovely. Will you find me another from Rose Leigh?", getReply(npc));

		en.step(player, "yes");
		assertEquals("Thank you! Once you find it, say #flower to me so I know you have it. I'll be sure to give you a nice reward.", getReply(npc));
		en.step(player, "flower");
		assertEquals("You don't seem to have a rhosyd bloom with you. But Rose Leigh wanders all over the island, I'm sure you'll find her one day!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, strange one.", getReply(npc));

		// -----------------------------------------------

		enRose.step(player, "hi");
		assertEquals("I gave you a flower not five minutes past! Her Royal Highness can enjoy that one for a while.", getReply(npcRose));
		assertEquals(ConversationStates.IDLE, enRose.getCurrentState());

		// -----------------------------------------------

		// Allow get flower a bit later;
		player.setQuest("elf_princess", 1, Long.toString(System.currentTimeMillis() - 5 * 60 * 1000 - 10));

		enRose.step(player, "hi");
		assertEquals("Hello dearie. My far sight tells me you need a pretty flower for some fair maiden. Here ye arr, bye now.", getReply(npcRose));
		assertEquals(ConversationStates.IDLE, enRose.getCurrentState());

		en.step(player, "hi");
		assertEquals("Hail to thee, human.", getReply(npc));
		en.step(player, "flower");
		assertTrue(getReply(npc).startsWith("Thank you! Take these "));
		// [00:10] superkym earns 5000 experience points.
		en.step(player, "bye");
		assertEquals("Goodbye, strange one.", getReply(npc));
	}
}
