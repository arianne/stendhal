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
package games.stendhal.server.maps.semos.kanmararn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.quests.JailedDwarf;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test for DwarfGuardNPC: sell chaos legs.
 *
 * @author Martin Fuchs
 */
public class DwarfGuardNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "-7_kanmararn_prison";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public DwarfGuardNPCTest() {
		setNpcNames("Hunel");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new DwarfGuardNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new JailedDwarf();
		quest.addToWorld();
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Hunel");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Hunel"));
		assertEquals("Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles.", getReply(npc));

		// Hunel doesn't listen to us until we get the prison key.
		assertFalse(en.step(player, "bye"));

		equipWithItem(player, "kanmararn prison key");

		assertTrue(en.step(player, "hi Hunel"));
		assertEquals("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye .. be careful ..", getReply(npc));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC npc = getNPC("Hunel");
		final Engine en = npc.getEngine();

		equipWithItem(player, "kanmararn prison key");

		assertTrue(en.step(player, "hi"));
		assertEquals("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm was the guard of this Prison. Until .. well you know the rest.", getReply(npc));

		assertTrue(en.step(player, "task"));
		assertEquals("I'm too scared to leave here yet... I'm waiting for someone to #offer me some better equipment.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I buy pairs of chaos legs, chaos swords, chaos shields, and suits of chaos armor.", getReply(npc));

		assertTrue(en.step(player, "sell chocolate"));
		assertEquals("Sorry, I don't buy chocolates.", getReply(npc));

		assertTrue(en.step(player, "sell chaos legs"));
		assertEquals("A pair of chaos legs is worth 8000. Do you want to sell it?", getReply(npc));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, then how else may I help you?", getReply(npc));

		assertTrue(en.step(player, "sell two chaos legs"));
		assertEquals("2 pairs of chaos legs are worth 16000. Do you want to sell them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many pairs of chaos legs.", getReply(npc));

		assertTrue(equipWithItem(player, "chaos legs"));
		assertTrue(en.step(player, "sell chaos leg"));
		assertEquals("A pair of chaos legs is worth 8000. Do you want to sell it?", getReply(npc));

		assertFalse(player.isEquipped("money", 8000));
		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", getReply(npc));
		assertTrue(player.isEquipped("money", 8000));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye .. be careful ..", getReply(npc));
	}

}
