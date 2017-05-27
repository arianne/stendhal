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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.city.GreeterNPC;
import games.stendhal.server.maps.semos.tavern.TraderNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class HatForMonogenesTest {

	private SpeakerNPC npc;
	private Engine en;
	private SpeakerNPC npcXin;
	private Engine enXin;
	private AbstractQuest quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		assertTrue(SingletonRepository.getNPCList().getNPCs().isEmpty());

	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() {

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new GreeterNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Monogenes");
		en = npc.getEngine();

		final ZoneConfigurator zoneConf = new TraderNPC();
		zoneConf.configureZone(new StendhalRPZone("int_semos_tavern"), null);
		npcXin = SingletonRepository.getNPCList().get("Xin Blanca");
		enXin = npcXin.getEngine();

		quest = new MeetMonogenes();
		quest.addToWorld();
		quest = new HatForMonogenes();
		quest.addToWorld();

	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final Player player = PlayerTestHelper.createPlayer("player");
		en.step(player, "hi");
		assertEquals(
				"Hello there, stranger! Don't be too intimidated if people are quiet and reserved... the fear of Blordrough and his forces has spread all over the country, and we're all a bit concerned. I can offer a few tips on socializing though, would you like to hear them?",
				getReply(npc));
		en.step(player, "no");
		assertEquals(
				"And how are you supposed to know what's happening? By reading the Semos Tribune? Hah! Bye, then.",
				getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", getReply(npc));
		en.step(player, "task");
		assertEquals(
				"Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...",
				getReply(npc));
		en.step(player, "hat");
		assertEquals(
				"You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?",
				getReply(npc));
		en.step(player, "no");
		assertEquals(
				"You surely have more importants things to do, and little time to do them in. I'll just stay here and freeze to death, I guess... *sniff*",
				getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", getReply(npc));
		en.step(player, "task");
		assertEquals(
				"Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...",
				getReply(npc));
		en.step(player, "hat");
		assertEquals(
				"You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?",
				getReply(npc));
		en.step(player, "yes");
		assertEquals("Thanks, my good friend. I'll be waiting here for your return!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		final Item item = ItemTestHelper.createItem("money", 25);
		player.getSlot("bag").add(item);
		enXin.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npcXin));
		enXin.step(player, "buy leather helmet");
		assertEquals("A leather helmet will cost 25. Do you want to buy it?", getReply(npcXin));
		enXin.step(player, "yes");
		assertEquals("Congratulations! Here is your leather helmet!", getReply(npcXin));
		enXin.step(player, "bye");
		assertEquals("Bye.", getReply(npcXin));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hey! Is that leather hat for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("I guess someone more fortunate will get his hat today... *sneeze*", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Hey! Is that leather hat for me?", getReply(npc));
		npc.remove("text");
		player.drop("leather helmet");
		int oldXP = player.getXP();
		en.step(player, "yes");
		assertEquals(oldXP, player.getXP());
		assertEquals(null, getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals(
				"Hey, my good friend, remember that leather hat I asked you about before? It's still pretty chilly here...",
				getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		player.equip("bag", SingletonRepository.getEntityManager().getItem("leather helmet"));
		en.step(player, "hi");
		assertEquals("Hey! Is that leather hat for me?", getReply(npc));
		oldXP = player.getXP();
		en.step(player, "yes");
		assertEquals(oldXP + 50, player.getXP());

		assertEquals("Bless you, my good friend! Now my head will stay nice and warm.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// (sorry i meant to put it on ground to test if he noticed it went
		// missing, i did, but i forgot i had one on my head too, he took that.)
	}

	/**
	 * Tests for getHistory.
	 */
	@Test
	public void testGetHistory() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		final List<String> history = new ArrayList<String>();
		assertEquals(history, quest.getHistory(player));

		player.setQuest("hat_monogenes", "");
		history.add("I have met Monogenes at the spring in Semos village");
		history.add("I have to find a hat, something leather to keep his head warm.");
		assertEquals(history, quest.getHistory(player));

		player.setQuest("hat_monogenes", "start");
		player.equip("bag", ItemTestHelper.createItem("leather helmet"));
		history.add("I have found a hat.");

		assertEquals(history, quest.getHistory(player));
		player.setQuest("hat_monogenes", "done");
		history.add("I gave the hat to Monogenes to keep his bald head warm.");

		assertEquals(history, quest.getHistory(player));

	}



}
