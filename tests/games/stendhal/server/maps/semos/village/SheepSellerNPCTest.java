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
package games.stendhal.server.maps.semos.village;

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

/**
 * Test buying sheep.
 *
 * @author Martin Fuchs
 */
public class SheepSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_semos_village_w";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	public SheepSellerNPCTest() {
		setNpcNames("Nishiya");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new SheepSellerNPC(), ZONE_NAME);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Nishiya");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for buySheep.
	 */
	@Test
	public void testBuySheep() {
		final SpeakerNPC npc = getNPC("Nishiya");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I work as a sheep seller.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell sheep.", getReply(npc));

		assertTrue(en.step(player, "buy"));
		assertEquals("A sheep will cost 30. Do you want to buy it?", getReply(npc));
		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", getReply(npc));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", getReply(npc));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", getReply(npc));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("A sheep will cost 30. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("A sheep will cost 30. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("You don't seem to have enough money.", getReply(npc));

		// equip with enough money to buy one sheep
		assertTrue(equipWithMoney(player, 30));

		assertTrue(en.step(player, "buy 2 sheep"));
		assertEquals("2 sheep will cost 60. Do you want to buy them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hmm... I just don't think you're cut out for taking care of a whole flock of sheep at once.", getReply(npc));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("A sheep will cost 30. Do you want to buy it?", getReply(npc));

		assertFalse(player.hasSheep());

		assertTrue(en.step(player, "yes"));
		assertEquals("Here you go, a nice fluffy little sheep! Take good care of it, now...", getReply(npc));

		assertTrue(player.hasSheep());
	}

	/**
	 * Tests for sellSheep.
	 */
	@Test
	public void testSellSheep() {
		final SpeakerNPC npc = getNPC("Nishiya");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Nishiya"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "sell"));
		assertEquals("Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.", getReply(npc));

		assertTrue(en.step(player, "sell sheep"));
		assertEquals("Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.", getReply(npc));
	}

}
