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
import static org.hamcrest.Matchers.lessThan;
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
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.nalwor.bank.BankNPC;
import games.stendhal.server.maps.nalwor.postoffice.PostNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class TakeGoldforGrafindleTest {


	private static String questSlot = "grafindle_gold";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new PostNPC().configureZone(zone, null);
		new BankNPC().configureZone(zone, null);

		final AbstractQuest quest = new TakeGoldforGrafindle();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for starting the quest.
	 */
	@Test
	public void testStartQuest() {

		npc = SingletonRepository.getNPCList().get("Grafindle");
		en = npc.getEngine();

		final double karma = player.getKarma();

		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", getReply(npc));
		en.step(player, "help");
		assertEquals("That room has two chests owned by this bank and two owned by Semos bank.", getReply(npc));
		en.step(player, "task");
		assertEquals("I need someone who can be trusted with #gold.", getReply(npc));
		en.step(player, "gold");
		assertEquals("One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.", getReply(npc));
		en.step(player, "Lorithien");
		assertEquals("She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?", getReply(npc));
		en.step(player, "no");
		assertEquals("Well, at least you are honest and told me from the start.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("rejected"));
		assertThat(player.getKarma(), lessThan(karma));

		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need someone who can be trusted with #gold.", getReply(npc));
		en.step(player, "gold");
		assertEquals("One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.", getReply(npc));
		// note we spelt this wrong but it was understood
		en.step(player, "Lorithiten");
		assertEquals("She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you. I hope to see you soon with the gold bars ... unless you are tempted to keep them.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("start"));

		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", getReply(npc));
		en.step(player, "task");
		assertEquals("I need someone who can be trusted with #gold.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", getReply(npc));

	}


	/**
	 * Tests for speaking to Lorithien to get the gold
	 */
	@Test
	public void testGetGold() {

		npc = SingletonRepository.getNPCList().get("Lorithien");
		en = npc.getEngine();

		player.setQuest(questSlot, "start");

		en.step(player, "hi");
		assertEquals("I'm so glad you're here! I'll be much happier when this gold is safely in the bank.", getReply(npc));
		assertTrue(player.isEquipped("gold bar", 25));
		assertThat(player.getQuest(questSlot), is("lorithien"));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", getReply(npc));

		en.step(player, "hi");
		assertEquals("Oh, please take that gold back to #Grafindle before it gets lost!", getReply(npc));
		en.step(player, "grafindle");
		assertEquals("Grafindle is the senior banker here in Nalwor, of course!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", getReply(npc));
	}

	/**
	 * Tests for taking the gold
	 */
	@Test
	public void testTakeGold() {

		npc = SingletonRepository.getNPCList().get("Grafindle");
		en = npc.getEngine();

		player.setQuest(questSlot, "lorithien");

		// the player in this case hasn't got the gold bars (we started a new test) so it's like he did get them but he put them on the ground

		en.step(player, "hi");
		assertEquals("Haven't you got the gold bars from #Lorithien yet? Please go get them, quickly!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", getReply(npc));

		// the player did actually get them before so lets now equip him with them
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 25);

		final int xp = player.getXP();
		final double karma3 = player.getKarma();

		en.step(player, "hi");
		// [09:40] kymara earns 200 experience points.
		assertEquals("Oh, you brought the gold! Wonderful, I knew I could rely on you. Please, have this key to our customer room.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", getReply(npc));
		assertThat(player.getXP(), greaterThan(xp));
		assertTrue(player.isQuestCompleted(questSlot));
		assertThat(player.getKarma(), greaterThan(karma3));
		assertFalse(player.isEquipped("gold bar"));
		assertTrue(player.isEquipped("nalwor bank key"));

		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", getReply(npc));
		en.step(player, "task");
		assertEquals("I ask only that you are honest.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", getReply(npc));
	}


	/**
	 * Tests for speaking to lorithien after completed quest
	 */
	@Test
	public void testSpeaToLorithienAfterQuestCompleted() {

		npc = SingletonRepository.getNPCList().get("Lorithien");
		en = npc.getEngine();

		player.setQuest(questSlot, "done");

		// return to post elf after quest done
		en.step(player, "hi");
		assertEquals("Hi, can I #help you?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", getReply(npc));
	}
}
