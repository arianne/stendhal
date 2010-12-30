/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2010-2011 - Stendhal                    *
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
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import marauroa.common.game.RPObject.ID;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ShopAssistantNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String QUEST1 = "erna_bake_bread";
	private static final String QUEST2 = "borrow_kitchen_equipment";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new ShopAssistantNPC().configureZone(zone, null);
		
		setupZone(ZONE_NAME);
	}

	public ShopAssistantNPCTest() {
		super(ZONE_NAME, "Erna");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		player.removeQuest(QUEST1);
		player.removeQuest(QUEST2);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
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
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I can only bake a loaf of bread if you bring me 2 #'sacks of flour'.",
				getReply(npc));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}


	/**
	 * Tests for baking a single bread.
	 */
	@Test
	public void testBakeSingleBread() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		final StackableItem flour = new StackableItem("flour", "", "", null);
		flour.setQuantity(2);
		flour.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(flour);
		assertEquals(2, player.getNumberOfEquipped("flour"));

		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 2 #'sacks of flour' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST1).split(";");
		final String[] expected = { "1", "bread", "" };
		assertEquals("amount", expected[0], questStatus[0]); 
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will bake a loaf of bread for you, but that will take some time. Please come back in 10 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("flour"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST1, "1;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have the loaf of bread.",
				getReply(npc));
		assertEquals(1, player.getNumberOfEquipped("bread"));
	}

	/**
	 * Tests for baking multiple breads.
	 */
	@Test
	public void testBakeMultipleBreads() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		final StackableItem flour = new StackableItem("flour", "", "", null);
		flour.setQuantity(4);
		flour.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(flour);
		assertEquals(4, player.getNumberOfEquipped("flour"));

		en.step(player, "make 2 breads");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 4 #'sacks of flour' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST1).split(";");
		final String[] expected = { "2", "bread", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will bake 2 loaves of bread for you, but that will take some time. Please come back in 20 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("flour"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST1, "2;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 2 loaves of bread.",
				getReply(npc));
		assertEquals(2, player.getNumberOfEquipped("bread"));
	}

	/**
	 * Tests for baking multiple breads without naming them.
	 */
	@Test
	public void testBakeMultipleWithoutName() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		final StackableItem flour = new StackableItem("flour", "", "", null);
		flour.setQuantity(6);
		flour.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(flour);
		assertEquals(6, player.getNumberOfEquipped("flour"));

		en.step(player, "make 3");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 6 #'sacks of flour' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST1).split(";");
		final String[] expected = { "3", "bread", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will bake 3 loaves of bread for you, but that will take some time. Please come back in 30 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("flour"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST1, "3;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 3 loaves of bread.",
				getReply(npc));
		assertEquals(3, player.getNumberOfEquipped("bread"));
	}

}
