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
package games.stendhal.server.maps.semos.bakery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import marauroa.common.game.RPObject.ID;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ChefNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String QUEST = "leander_make_sandwiches";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ChefNPCTest() {
		setNpcNames("Leander");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new ChefNPC(), ZONE_NAME);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		player.removeQuest(QUEST);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {

		final SpeakerNPC npc = getNPC("Leander");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for hiAndMakeNoStuff.
	 */
	@Test
	public void testHiAndMakeNoStuff() {
		final SpeakerNPC npc = getNPC("Leander");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I can only make a sandwich if you bring me 2 #'pieces of cheese', a #'loaf of bread', and a #'piece of ham'.",
				getReply(npc));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for hiAndMakeWithStuffSingle.
	 */
	@Test
	public void testHiAndMakeWithStuffSingle() {
		final SpeakerNPC npc = getNPC("Leander");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(2);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(1);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(2, player.getNumberOfEquipped("cheese"));
		assertEquals(1, player.getNumberOfEquipped("bread"));
		assertEquals(1, player.getNumberOfEquipped("ham"));

		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 2 #'pieces of cheese', a #'loaf of bread', and a #'piece of ham' for this job, which will take 3 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "1", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]);

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make a sandwich for you, but that will take some time. Please come back in 3 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "1;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have the sandwich.",
				getReply(npc));
		assertEquals(1, player.getNumberOfEquipped("sandwich"));
	}

	/**
	 * Tests for hiAndMakeWithStuffMultiple.
	 */
	@Test
	public void testHiAndMakeWithStuffMultiple() {
		final SpeakerNPC npc = getNPC("Leander");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(4);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(2);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(2);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(4, player.getNumberOfEquipped("cheese"));
		assertEquals(2, player.getNumberOfEquipped("bread"));
		assertEquals(2, player.getNumberOfEquipped("ham"));

		en.step(player, "make 2 sandwiches");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 2 #'loaves of bread', 2 #'pieces of ham', and 4 #'pieces of cheese' for this job, which will take 6 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "2", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]);

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 2 sandwiches for you, but that will take some time. Please come back in 6 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "2;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 2 sandwiches.",
				getReply(npc));
		assertEquals(2, player.getNumberOfEquipped("sandwich"));
	}

	/**
	 * Tests for multipleWithoutName.
	 */
	@Test
	public void testMultipleWithoutName() {
		final SpeakerNPC npc = getNPC("Leander");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(6);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(3);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(10);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(6, player.getNumberOfEquipped("cheese"));
		assertEquals(3, player.getNumberOfEquipped("bread"));
		assertEquals(10, player.getNumberOfEquipped("ham"));

		en.step(player, "make 3");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 3 #'loaves of bread', 3 #'pieces of ham', and 6 #'pieces of cheese' for this job, which will take 9 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "3", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]);

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 3 sandwiches for you, but that will take some time. Please come back in 9 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		// 10 - 3 -> 7
		assertEquals(7, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "3;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 3 sandwiches.",
				getReply(npc));
		assertEquals(3, player.getNumberOfEquipped("sandwich"));
	}

}
