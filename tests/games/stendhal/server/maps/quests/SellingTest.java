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
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.bar.BarMaidNPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test selling cheese to the bar maid.
 *
 * @author Martin Fuchs
 */
public class SellingTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_bar";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new BarMaidNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public SellingTest() {
		super(ZONE_NAME, "Siandra");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Siandra");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Siandra"));
		assertEquals("Hi!", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye bye!", getReply(npc));
	}

	/**
	 * Tests for selling.
	 */
	@Test
	public void testSelling() {
		final SpeakerNPC npc = getNPC("Siandra");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi!", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?", getReply(npc));

		assertTrue(en.step(player, "task"));
		assertEquals("Just #offers of food is enough, thank you.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I buy items of these kinds: cheese, meat, spinach, ham, flour, and porcini.", getReply(npc));

		assertTrue(en.step(player, "sell"));
		assertEquals("Please tell me what you want to sell.", getReply(npc));

		assertTrue(en.step(player, "sell house"));
		assertEquals("Sorry, I don't buy any houses.", getReply(npc));

		assertTrue(en.step(player, "sell cheese"));
		assertEquals("A piece of cheese is worth 5. Do you want to sell it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have any piece of cheese.", getReply(npc));

		 // equip the player with enough cheese to be sold
		assertFalse(player.isEquipped("cheese", 1));
		assertTrue(equipWithStackableItem(player, "cheese", 3));
        assertTrue(player.isEquipped("cheese", 3));
        assertFalse(player.isEquipped("cheese", 4));

		assertTrue(en.step(player, "sell cheese"));
		assertEquals("A piece of cheese is worth 5. Do you want to sell it?", getReply(npc));

		 // ensure we currently don't have any money
		assertFalse(player.isEquipped("money", 1));

		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", getReply(npc));

		 // check if we got the promised money and the cheese is gone into Siandra's hands
		assertTrue(player.isEquipped("money", 5));
        assertTrue(player.isEquipped("cheese", 2));
        assertFalse(player.isEquipped("cheese", 3));

		 // test what happens when trying to sell nothing
		assertTrue(en.step(player, "sell 0 cheese"));
		assertEquals("Sorry, how many pieces of cheese do you want to sell?!", getReply(npc));
		assertFalse(en.step(player, "yes"));

		 // test what happens when trying to sell even less than nothing
		assertTrue(en.step(player, "sell -5 cheese"));
		assertEquals("Sorry, I did not understand you. negative amount: -5", getReply(npc));
		assertFalse(en.step(player, "yes"));
	}

}
