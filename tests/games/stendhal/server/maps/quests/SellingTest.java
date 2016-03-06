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

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.bar.BarMaidNPC;
import games.stendhal.server.maps.semos.tavern.RareWeaponsSellerNPC;
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
		setupZone(ZONE_NAME);
	}

	public SellingTest() {
		setNpcNames("Siandra", "McPegleg");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new RareWeaponsSellerNPC(), ZONE_NAME);
		addZoneConfigurator(new BarMaidNPC(), ZONE_NAME);
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
		assertEquals("I buy pieces of cheese, pieces of meat, spinaches, pieces of ham, sacks of flour, and porcini.", getReply(npc));

		assertTrue(en.step(player, "sell"));
		assertEquals("Please tell me what you want to sell.", getReply(npc));

		assertTrue(en.step(player, "sell house"));
		assertEquals("Sorry, I don't buy houses.", getReply(npc));

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

	/**
	 * Tests for selling porcini.
	 */
	@Test
	public void testSellPorcini() {
		final SpeakerNPC npc = getNPC("Siandra");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi!", getReply(npc));

		assertTrue(en.step(player, "sell porcini"));
		assertEquals("A porcino is worth 30. Do you want to sell it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have any porcino.", getReply(npc));

		assertTrue(en.step(player, "sell 2 porcini"));
		assertEquals("2 porcini are worth 60. Do you want to sell them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many porcini.", getReply(npc));

		assertTrue(en.step(player, "sell 99 porcinis")); // misspelled plural instead of the correct "porcini"
		assertEquals("99 porcini are worth 2970. Do you want to sell them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many porcini.", getReply(npc));

		 // equip the player with enough porcini to be sold
		assertFalse(player.isEquipped("porcini", 1));
		assertTrue(equipWithStackableItem(player, "porcini", 3));
        assertTrue(player.isEquipped("porcini", 3));
        assertFalse(player.isEquipped("porcini", 4));

		assertTrue(en.step(player, "sell porcino"));
		assertEquals("A porcino is worth 30. Do you want to sell it?", getReply(npc));

		 // ensure we currently don't have any money
		assertFalse(player.isEquipped("money", 1));

		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", getReply(npc));

		 // check if we got the promised money and the cheese is gone into Siandra's hands
		assertTrue(player.isEquipped("money", 5));
        assertTrue(player.isEquipped("porcini", 2));
        assertFalse(player.isEquipped("porcini", 3));
	}

	/**
	 * Tests for selling solid plate shields.
	 */
	@Test
	public void testSellShields() {
		final SpeakerNPC npc = getNPC("McPegleg");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));

		assertTrue(en.step(player, "sell plate shield"));
		assertEquals("A plate shield is worth 40. Do you want to sell it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have any plate shield.", getReply(npc));

		assertTrue(en.step(player, "sell 2 plate shields"));
		assertEquals("2 plate shields are worth 80. Do you want to sell them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many plate shields.", getReply(npc));

		assertTrue(en.step(player, "sell 4 solid plate shields"));
		assertEquals("4 plate shields are worth 160. Do you want to sell them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many plate shields.", getReply(npc));

		 // equip the player with four plate shields to be sold
		assertFalse(player.isEquipped("plate shield", 1));
		assertTrue(equipWithItem(player, "plate shield"));
		assertTrue(equipWithItem(player, "plate shield"));
		assertTrue(equipWithItem(player, "plate shield"));
		assertTrue(equipWithItem(player, "plate shield"));
        assertTrue(player.isEquipped("plate shield", 4));
        assertFalse(player.isEquipped("plate shield", 5));

		assertTrue(en.step(player, "sell four plate shields"));
		assertEquals("4 plate shields are worth 160. Do you want to sell them?", getReply(npc));

		 // ensure we currently don't have any money
		assertFalse(player.isEquipped("money", 1));

		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", getReply(npc));

		 // check if we got the promised money and the cheese is gone into McPegleg's hands
		assertTrue(player.isEquipped("money", 5));
        assertFalse(player.isEquipped("plate shield", 1));
	}
}
