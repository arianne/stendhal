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

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject.ID;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ShopAssistantNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String QBAKEBREAD = "erna_bake_bread";
	private static final String BORROW = "borrow_kitchen_equipment";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ShopAssistantNPCTest() {
		setNpcNames("Erna");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new ShopAssistantNPC(), ZONE_NAME);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		player.removeQuest(QBAKEBREAD);
		player.removeQuest(BORROW);
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
				"I need you to fetch me 2 #'sacks of flour' for this job, which will take 10 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QBAKEBREAD).split(";");
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
		player.setQuest(QBAKEBREAD, "1;;0");

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
				"I need you to fetch me 4 #'sacks of flour' for this job, which will take 20 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QBAKEBREAD).split(";");
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
		player.setQuest(QBAKEBREAD, "2;;0");

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
				"I need you to fetch me 6 #'sacks of flour' for this job, which will take 30 minutes. Do you have what I need?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QBAKEBREAD).split(";");
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
		player.setQuest(QBAKEBREAD, "3;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 3 loaves of bread.",
				getReply(npc));
		assertEquals(3, player.getNumberOfEquipped("bread"));
	}


	/**
	 * Tests for borrowing the sugar mill.
	 */
	@Test
	public void testBorrowSugarMill() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"Oh sorry, I don't lend equipment to people with so little experience as you.",
				getReply(npc));

		// level up
		player.setLevel(10);
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				getReply(npc));

		player.setQuest("pizza_delivery", "done");
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"I lend out #'sugar mill', #'pestle and mortar', and #'rotary cutter'. If you're interested, please say which you want.",
				getReply(npc));

		en.step(player, "sugar mill");
		assertTrue(npc.isTalking());
		assertEquals(
				"Here you are! Don't forget to #return it or you have to pay!",
				getReply(npc));
		final String[] questStatus = player.getQuest(BORROW).split(";");
		assertEquals("sugar mill", questStatus[0]);

		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(BORROW, ";");

		assertEquals(1, player.getNumberOfEquipped("sugar mill"));

		en.step(player, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
	}

	/**
	 * Tests for borrowing sugar.
	 */
	@Test
	public void testBorrowSugar() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"Oh sorry, I don't lend equipment to people with so little experience as you.",
				getReply(npc));

		// level up
		player.setLevel(10);
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				getReply(npc));

		player.setQuest("pizza_delivery", "done");
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"I lend out #'sugar mill', #'pestle and mortar', and #'rotary cutter'. If you're interested, please say which you want.",
				getReply(npc));

		en.step(player, "sugar");
		assertTrue(npc.isTalking());
		assertEquals(
				"Sorry, I can't lend out sugar, only a #sugar #mill.",
				getReply(npc));

		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(BORROW, ";");

		assertEquals(0, player.getNumberOfEquipped("sugar mill"));

		en.step(player, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
	}

	/**
	 * Tests for borrowing pestle and mortar.
	 */
	@Test
	public void testBorrowPestleMortar() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"Oh sorry, I don't lend equipment to people with so little experience as you.",
				getReply(npc));

		// level up
		player.setLevel(10);
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				getReply(npc));

		player.setQuest("pizza_delivery", "done");
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"I lend out #'sugar mill', #'pestle and mortar', and #'rotary cutter'. If you're interested, please say which you want.",
				getReply(npc));

		en.step(player, "pestle and mortar");
		assertTrue(npc.isTalking());
		assertEquals(
				"Here you are! Don't forget to #return it or you have to pay!",
				getReply(npc));
		final String[] questStatus = player.getQuest(BORROW).split(";");
		assertEquals("pestle and mortar", questStatus[0]);

		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(BORROW, ";");

		assertEquals(1, player.getNumberOfEquipped("pestle and mortar"));

		en.step(player, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
	}


	/**
	 * Tests for borrowing pestle and mortar with additional space.
	 */
	@Test
	public void testBorrowPestleAndMortarWithSpace() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"Oh sorry, I don't lend equipment to people with so little experience as you.",
				getReply(npc));

		// level up
		player.setLevel(10);
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				getReply(npc));

		player.setQuest("pizza_delivery", "done");
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"I lend out #'sugar mill', #'pestle and mortar', and #'rotary cutter'. If you're interested, please say which you want.",
				getReply(npc));

		en.step(player, "pestle and  mortar");
		assertTrue(npc.isTalking());
		assertEquals(
				"Here you are! Don't forget to #return it or you have to pay!",
				getReply(npc));
		final String[] questStatus = player.getQuest(BORROW).split(";");
		assertEquals("pestle and mortar", questStatus[0]);

		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(BORROW, ";");

		assertEquals(1, player.getNumberOfEquipped("pestle and mortar"));

		en.step(player, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
	}


	/**
	 * Tests for borrowing rotary cutter.
	 */
	@Test
	public void testBorrowRotaryCutter() {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine en = npc.getEngine();

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"Oh sorry, I don't lend equipment to people with so little experience as you.",
				getReply(npc));

		// level up
		player.setLevel(10);
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"You'll have to speak to Leander and ask if you can help with the pizza before I'm allowed to lend you anything.",
				getReply(npc));

		player.setQuest("pizza_delivery", "done");
		en.step(player, "borrow");
		assertTrue(npc.isTalking());
		assertEquals(
				"I lend out #'sugar mill', #'pestle and mortar', and #'rotary cutter'. If you're interested, please say which you want.",
				getReply(npc));

		en.step(player, "rotary cutter");
		assertTrue(npc.isTalking());
		assertEquals(
				"Here you are! Don't forget to #return it or you have to pay!",
				getReply(npc));
		final String[] questStatus = player.getQuest(BORROW).split(";");
		assertEquals("rotary cutter", questStatus[0]);

		en.step(player, "bye");
		assertFalse(npc.isTalking());
		//player.setQuest(BORROW, ";");

		assertEquals(1, player.getNumberOfEquipped("rotary cutter"));

		en.step(player, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));

		en.step(player, "return");
		assertEquals("Do you want to return what you borrowed now?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! Just let me know if you want to #borrow any tools again.", getReply(npc));
		assertFalse(player.isEquipped("rotary cutter"));
	}


/**
 * Erna dialog - ID: 3427142
Last Update: Comment added ( wmbec7718 )
Details:

Erna stops conversation if you need to pay for a rented item, but don't have enough money in bag (3000).
I didn't go further and pay the 3000, hope to get that item back from Ortiv to save. ? thanks geomac.

 * @throws Exception
 */
@Test
	public void brokePlayer() throws Exception {
		final SpeakerNPC npc = getNPC("Erna");
		final Engine engine = npc.getEngine();
		Player brokePlayer = PlayerTestHelper.createPlayer("brokePlayer");
		brokePlayer.setQuest("pizza_delivery", "done");
		brokePlayer.setLevel(10);
		brokePlayer.setQuest(BORROW, "start");
		engine.step(brokePlayer, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		engine.step(brokePlayer, "sugar mill");
		assertEquals(
				"You can't borrow from me again till you #return the last tool I lent you.",
				getReply(npc));
		engine.step(brokePlayer, "return");
		assertEquals(
				"You don't have it with you! Do you want to pay 3000 money for it now?",
				getReply(npc));
		engine.step(brokePlayer, "no");
		assertEquals(
				"No problem. Take as long as you need, but you can't borrow other tools till you return the last, or pay for it.",
				getReply(npc));
		engine.step(brokePlayer, "bye");
		assertEquals("Bye.", getReply(npc));

		engine.step(brokePlayer, "hi");
		assertEquals(
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.",
				getReply(npc));
		engine.step(brokePlayer, "sugar mill");
		assertEquals(
				"You can't borrow from me again till you #return the last tool I lent you.",
				getReply(npc));
		engine.step(brokePlayer, "return");
		assertEquals(
				"You don't have it with you! Do you want to pay 3000 money for it now?",
				getReply(npc));
		engine.step(brokePlayer, "yes");
		assertEquals(
				"Sorry, but it seems you dont have enough money with you.",
				getReply(npc));
	}
}
