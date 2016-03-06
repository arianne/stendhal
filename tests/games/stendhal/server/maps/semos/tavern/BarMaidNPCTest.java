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
package games.stendhal.server.maps.semos.tavern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test buying with fractional amounts.
 *
 * @author Martin Fuchs
 */
public class BarMaidNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_semos_tavern_0";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	public BarMaidNPCTest() {
		setNpcNames("Margaret");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new BarMaidNPC(), ZONE_NAME);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Margaret");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for buyHam.
	 */
	@Test
	public void testBuyHam() {
		final SpeakerNPC npc = getNPC("Margaret");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I am the bar maid for this fair tavern. You can #buy both imported and local beers, and fine food.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell beer, wine, flask, cheese, apple, carrot, meat, and ham.", getReply(npc));

		assertTrue(en.step(player, "quest"));
		assertEquals("Oh nice that you ask me. Unfortunately I have nothing to do for you.", getReply(npc));


		assertTrue(en.step(player, "buy"));
		assertEquals("Please tell me what you want to buy.", getReply(npc));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", getReply(npc));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", getReply(npc));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", getReply(npc));

		assertTrue(en.step(player, "buy ham"));
		assertEquals("A piece of ham will cost 80. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy ham"));
		assertEquals("A piece of ham will cost 80. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", getReply(npc));

		// equip with enough money
		assertTrue(equipWithMoney(player, 2000));

		assertFalse(player.isEquipped("ham"));
		assertTrue(en.step(player, "buy 5 hams"));
		assertEquals("5 pieces of ham will cost 400. Do you want to buy them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your pieces of ham!", getReply(npc));
		assertTrue(player.isEquipped("ham", 5));

		assertTrue(en.step(player, "buy ham"));
		assertEquals("A piece of ham will cost 80. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here is your piece of ham!", getReply(npc));

		// test handling of fractional numbers
		assertTrue(en.step(player, "buy 2.71828 ham"));
		assertEquals("3 pieces of ham will cost 240. Do you want to buy them?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your pieces of ham!", getReply(npc));

		// now with comma instead of dot
		assertTrue(en.step(player, "buy 3,5 ham"));
		assertEquals("4 pieces of ham will cost 320. Do you want to buy them?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your pieces of ham!", getReply(npc));

		// test illegal number formats
		assertTrue(en.step(player, "buy 2.718.28 ham"));
		assertEquals("Sorry, I did not understand you. illegal number format: '2.718.28'", getReply(npc));
		assertTrue(en.step(player, "buy 2,718,28 ham"));
		assertEquals("Sorry, I did not understand you. illegal number format: '2,718,28'", getReply(npc));

		assertTrue(en.step(player, "buy 1000 ham"));
        assertEquals("1000 pieces of ham will cost 80000. Do you want to buy them?", getReply(npc));
        assertTrue(en.step(player, "no"));
        assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy 10000 ham"));
		assertEquals("Sorry, the maximum number of ham which I can sell at once is 1000.", getReply(npc));
	}

	/**
	 * Tests for sellHam.
	 */
	@Test
	public void testSellHam() {
		final SpeakerNPC npc = SingletonRepository.getNPCList().get("Margaret");
		assertNotNull(npc);
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Margaret"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		// Currently there are no response to "sell" sentences defined for Margaret.
		assertFalse(en.step(player, "sell"));
	}

}
