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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.storage.HousewifeNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class CleanStorageSpaceTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public CleanStorageSpaceTest() {
		setNpcNames("Eonna");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new HousewifeNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		final CleanStorageSpace cf = new CleanStorageSpace();
		cf.addToWorld();
	}

	/**
	 * Tests for hiAndbye.
	 */
	@Test
	public void testHiAndbye() {
		assertTrue(!player.hasKilled("rat"));

		final SpeakerNPC npc = getNPC("Eonna");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi there, young hero.", getReply(npc));
		assertTrue(en.step(player, "job"));
		assertTrue(npc.isTalking());
		assertEquals("I'm just a regular housewife.", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertTrue(npc.isTalking());
		assertEquals("Oh I love the bakery products by Leander. His sandwiches are awesome! Did you know, that he is searching for a helping hand?",
				getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void doQuest() {
		final SpeakerNPC npc = getNPC("Eonna");
		final Engine en = npc.getEngine();
		assertFalse(npc.isTalking());

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi there, young hero.", getReply(npc));
		assertTrue(en.step(player, "task"));
		assertTrue(npc.isTalking());
		assertEquals(
				"My #basement is absolutely crawling with rats. Will you help me?",
				getReply(npc));
		assertTrue(en.step(player, "basement"));
		assertTrue(npc.isTalking());
		assertEquals(
				"Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?",
				getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals(
				"Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!",
				getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
		player.setSoloKill("rat");
		assertTrue(player.hasKilled("rat"));
		player.setSharedKill("caverat");
		player.setSharedKill("snake");
		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("A hero at last! Thank you!", getReply(npc));

		assertEquals("done", player.getQuest("clean_storage"));
	}

}
