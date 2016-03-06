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
package games.stendhal.server.maps.semos.plains;

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
 * Test for ExperiencedWarriorNPC: Starkad.
 *
 * @author Christian Schnepf
 */
public class ExperiencedWarriorNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_semos_plains_s";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ExperiencedWarriorNPCTest() {
		setNpcNames("Starkad");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new ExperiencedWarriorNPC(), ZONE_NAME);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Starkad");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Farewell and godspeed!", getReply(npc));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC npc = getNPC("Starkad");
		final Engine en = npc.getEngine();

		//test the basic messages
		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("My job? I'm a well known warrior, strange that you haven't heard of me!", getReply(npc));

		assertTrue(en.step(player, "quest"));
		assertEquals("Thanks, but I don't need any help at the moment.", getReply(npc));

		assertTrue(en.step(player, "help"));
		assertEquals("If you want, I can tell you about the #creatures I have encountered.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I offer you information on #creatures I've seen for a reasonable fee.", getReply(npc));

		assertTrue(en.step(player, "creatures"));
		assertEquals("Which creature you would like to hear more about?", getReply(npc));

		//do a false monster test
		assertTrue(en.step(player, "C-Monster"));
		assertEquals("I have never heard of such a creature! Please tell the name again.", getReply(npc));

		//test with not having enough cash
		assertTrue(en.step(player, "angel"));
		assertEquals("This information costs 692. Are you still interested?", getReply(npc));

		assertFalse(player.isEquipped("money", 692));
		assertTrue(en.step(player, "yes"));
		assertEquals("You don't have enough money with you.", getReply(npc));

		//test with having the cash
		assertTrue(equipWithMoney(player, 692));
		assertTrue(player.isEquipped("money", 692));

		assertTrue(en.step(player, "creatures"));
		assertEquals("Which creature you would like to hear more about?", getReply(npc));

		assertTrue(en.step(player, "angel"));
		assertEquals("This information costs 692. Are you still interested?", getReply(npc));

		//lazy assertion since the phrases differ each time.  assume that the npc repeats the creature
		assertTrue(en.step(player, "yes"));
		assertTrue(getReply(npc).toLowerCase().contains("angel"));

		//say goodbye
		assertTrue(en.step(player, "bye"));
		assertEquals("Farewell and godspeed!", getReply(npc));
	}
}
