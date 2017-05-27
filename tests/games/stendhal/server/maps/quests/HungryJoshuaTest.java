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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.goldsmith.GoldsmithNPC;
import games.stendhal.server.maps.semos.blacksmith.BlacksmithNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class HungryJoshuaTest {


	private static String questSlot = "hungry_joshua";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new GoldsmithNPC().configureZone(zone, null);
		new BlacksmithNPC().configureZone(zone, null);

		final AbstractQuest quest = new HungryJoshua();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Xoderos");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm worried about my brother who lives in Ados. I need someone to take some #food to him.", getReply(npc));
		en.step(player, "food");
		assertEquals("I think five sandwiches would be enough. My brother is called #Joshua. Can you help?", getReply(npc));
		en.step(player, "joshua");
		assertEquals("He's the goldsmith in Ados. They're so short of supplies. Will you help?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you. Please tell him #food or #sandwich so he knows you're not just a customer.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("start"));
		en.step(player, "food");
		assertEquals("#Joshua will be getting hungry! Please hurry!", getReply(npc));
		en.step(player, "joshua");
		assertEquals("My brother, the goldsmith in Ados.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", getReply(npc));
		en.step(player, "task");
		assertEquals("Please don't forget the five #sandwiches for #Joshua!", getReply(npc));
		en.step(player, "sandwiches");
		assertEquals("#Joshua will be getting hungry! Please hurry!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------


		// -----------------------------------------------


		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Joshua");
		en = npc.getEngine();

		Item item = ItemTestHelper.createItem("sandwich", 5);
		player.getSlot("bag").add(item);
		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("Hi! I'm the local goldsmith. If you require me to #cast you a #'gold bar' just tell me!", getReply(npc));
		en.step(player, "food");
		assertEquals("Oh great! Did my brother Xoderos send you with those sandwiches?", getReply(npc));
		en.step(player, "yes");
		// [07:28] kymara earns 150 experience points.
		assertEquals("Thank you! Please let Xoderos know that I am fine. Say my name, Joshua, so he knows that you saw me. He will probably give you something in return.", getReply(npc));
		assertFalse(player.isEquipped("sandwich"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("joshua"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Xoderos");
		en = npc.getEngine();
		final int xp2 = player.getXP();

		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", getReply(npc));
		en.step(player, "task");
		assertEquals("I do hope #Joshua is well ....", getReply(npc));
		en.step(player, "food");
		assertEquals("I wish you could confirm for me that #Joshua is fine ...", getReply(npc));
		en.step(player, "joshua");
		// [07:29] kymara earns 50 experience points.
		assertEquals("I'm glad Joshua is well. Now, what can I do for you? I know, I'll fix that broken key ring that you're carrying ... there, it should work now!", getReply(npc));
		assertThat(player.getXP(), greaterThan(xp2));
		assertTrue(player.isQuestCompleted(questSlot));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
