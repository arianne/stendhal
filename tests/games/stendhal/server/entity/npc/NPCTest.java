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
package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.felinashouse.CatSellerNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CatTestHelper;

/**
 * Test NPC logic.
 *
 * @author Martin Fuchs
 */
public class NPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CatTestHelper.generateRPClasses();
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public NPCTest() {
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
	 * Tests for logic.
	 */
	@Test
	public void testLogic() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		npc.listenTo(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", getReply(npc));

		assertNotNull(npc.getAttending());
		npc.preLogic();
		assertEquals("Bye.", getReply(npc));
		assertEquals(null, npc.getAttending());
	}

	/**
	 * Tests for idea.
	 */
	@Test
	public void testIdea() {
		final SpeakerNPC npc = getNPC("Felina");

		assertEquals("awaiting", npc.getIdea());
		npc.setIdea("walk");
		assertEquals("walk", npc.getIdea());

		npc.setIdea(null);
		assertEquals(null, npc.getIdea());

		npc.setIdea("awaiting");
		assertEquals("awaiting", npc.getIdea());
	}

	// players use _hi, _hello etc to avoid npcs answering when it's meant to
	// other players
	/**
	 * Tests for underscore.
	 */
	@Test
	public void testUnderscore() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final SpeakerNPC npc = getNPC("Felina");
			final Engine en = npc.getEngine();

			assertEquals(ConversationStates.IDLE, en.getCurrentState());

			en.step(player, "_" + hello);
			assertEquals("npc should not answer to _" + hello, ConversationStates.IDLE, en.getCurrentState());
		}
	}
}
