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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.bank.CustomerAdvisorNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class ArmorForDagobertTest {
	private Player player;
	private SpeakerNPC npc;
	private Engine en;
	private AbstractQuest quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new CustomerAdvisorNPC().configureZone(zone, null);

		npc = SingletonRepository.getNPCList().get("Dagobert");
		quest = new ArmorForDagobert();
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
		assertEquals("Welcome to the bank of Semos! I am here to #help you manage your personal chest.", getReply(npc));
		assertTrue(quest.getHistory(player).isEmpty());

		en.step(player, "no");

		assertTrue(quest.getHistory(player).isEmpty());
		en.step(player, "task");
		assertTrue(quest.getHistory(player).isEmpty());
		assertEquals("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?",
				getReply(npc));
		en.step(player, "no");
		java.util.List<String> questHistory = new LinkedList<String>();
		questHistory.add("I have met Dagobert. He is the consultant at the bank in Semos.");
		questHistory.add("He asked me to find a leather cuirass but I rejected his request.");
		assertEquals(questHistory, quest.getHistory(player));

		assertEquals("Well, then I guess I'll just duck and cover.", getReply(npc));
		en.step(player, "bye");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("It was a pleasure to serve you.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Welcome to the bank of Semos! I am here to #help you manage your personal chest.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "task");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?",
				getReply(npc));
		en.step(player, "yes");
		questHistory = new LinkedList<String>();
		questHistory.add("I have met Dagobert. He is the consultant at the bank in Semos.");
		questHistory.add("I promised to find a leather cuirass for him because he has been robbed.");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals(
				"Once I had a nice #'leather cuirass', but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.",
				getReply(npc));
		en.step(player, "leather");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals(
				"A leather cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.",
				getReply(npc));
		en.step(player, "bye");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("It was a pleasure to serve you.", getReply(npc));

		// -----------------------------------------------
		final Item item = ItemTestHelper.createItem("leather cuirass");
		player.getSlot("bag").add(item);
		questHistory.add("I found a leather cuirass and will take it to Dagobert.");
		assertEquals(questHistory, quest.getHistory(player));

		en.step(player, "hi");
		assertEquals("Excuse me, please! I have noticed the leather cuirass you're carrying. Is it for me?",
				getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "no");
		assertEquals("Well then, I hope you find another one which you can give to me before I get robbed again.",
				getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("It was a pleasure to serve you.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Excuse me, please! I have noticed the leather cuirass you're carrying. Is it for me?",
				getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		// put it out of bag onto ground, then say yes.
		player.drop("leather cuirass");
		assertFalse(player.isEquipped("leather cuirass"));
		questHistory.remove("I found a leather cuirass and will take it to Dagobert.");
		assertEquals(questHistory, quest.getHistory(player));
		npc.remove("text");
		en.step(player, "yes");
		// he doesn't do anything.
		assertEquals(questHistory, quest.getHistory(player));
		assertFalse(npc.has("text"));
		en.step(player, "bye");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("It was a pleasure to serve you.", getReply(npc));

		// -----------------------------------------------

		player.getSlot("bag").add(item);
		en.step(player, "hi");
		questHistory.add("I found a leather cuirass and will take it to Dagobert.");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("Excuse me, please! I have noticed the leather cuirass you're carrying. Is it for me?",
				getReply(npc));
		final int xpBeforeReward = player.getXP();
		en.step(player, "yes");
		questHistory.add("I took the leather cuirass to Dagobert. As a little thank you, he will allow me to use a private vault.");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere. Now that you have proven yourself a trusted customer, you may have access to your own private banking #vault any time you like.", getReply(npc));
		assertEquals(xpBeforeReward + 50, player.getXP());
		en.step(player, "task");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("Thank you very much for the armor, but I don't have any other task for you.", getReply(npc));
	}


}
