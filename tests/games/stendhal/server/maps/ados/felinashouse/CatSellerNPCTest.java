/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.felinashouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CatTestHelper;

/**
 * Test buying cats.
 * @author Martin Fuchs
 */
public class CatSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CatTestHelper.generateRPClasses();
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	public CatSellerNPCTest() {
		setNpcNames("Felina");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new CatSellerNPC(), ZONE_NAME);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Felina"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for buyCat.
	 */
	@Test
	public void testBuyCat() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", getReply(npc));

		assertTrue(en.step(player, "care"));
		assertEquals("Cats love chicken and fish. Just place a piece on the ground and your cat will run over to eat it. You can right-click on her and choose 'Look' at any time, to check up on her weight; she will gain one unit of weight for every piece of chicken she eats.", getReply(npc));

		// There is currently no quest response defined for Felina.
		assertFalse(en.step(player, "quest"));

		assertTrue(en.step(player, "buy"));
		assertEquals("A cat will cost 100. Do you want to buy it?", getReply(npc));
		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", getReply(npc));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", getReply(npc));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", getReply(npc));

		assertTrue(en.step(player, "buy a glass of wine"));
		assertEquals("Sorry, I don't sell glasses of wine.", getReply(npc));

		assertTrue(en.step(player, "buy a hand full of peace"));
		assertEquals("Sorry, I don't sell hand fulls of peace.", getReply(npc));

		assertTrue(en.step(player, "buy cat"));
		assertEquals("A cat will cost 100. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy cat"));
		assertEquals("A cat will cost 100. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("You don't seem to have enough money.", getReply(npc));

		assertTrue(en.step(player, "buy two cats"));
		assertEquals("2 cats will cost 200. Do you want to buy them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hmm... I just don't think you're cut out for taking care of more than one cat at once.", getReply(npc));

		// equip with enough money to buy the cat
		assertTrue(equipWithMoney(player, 500));
		assertTrue(en.step(player, "buy cat"));
		assertEquals("A cat will cost 100. Do you want to buy it?", getReply(npc));

		assertFalse(player.hasPet());

		assertTrue(en.step(player, "yes"));
		assertEquals("Here you go, a cute little kitten! Your kitten will eat any piece of chicken or fish you place on the ground. Enjoy her!", getReply(npc));

		assertTrue(player.hasPet());

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for sellCat.
	 */
	@Test
	public void testSellCat() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "sell cat"));
		assertEquals("Sell??? What kind of a monster are you? Why would you ever sell your beautiful cat?", getReply(npc));
	}

}
