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
package games.stendhal.server.maps.semos.house;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class AdminHelpNPCTest extends PlayerTestHelper {

	private Engine en;

	private Player player;

	private SpeakerNPC npc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@Before
	public void setUp() throws Exception {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new AdminHelpNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Skye");
		en = npc.getEngine();
		player = createPlayer("bob");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hello! You're looking particularly good today. In fact, you look great every day!",
				getReply(npc));
		en.step(player, "job");
		assertEquals(
				"I'm here to make you feel happy. And you can come here easily if you #/teleportto me. Also, I can explain the #portals here.",
				getReply(npc));
		en.step(player, "help");
		assertEquals(
				"I can #heal you if you like. Or I can just say #nice #things. If you need to know about the #portals, just ask.",
				getReply(npc));
		en.step(player, "nice");
		assertEquals(
				"Did you know how many players think you're lovely for helping? Well I can tell you, loads of them do.",
				getReply(npc));
		en.step(player, "things");
		assertEquals(
				"So you're one of the people who tests all the #blue #words, aren't you? Now wonder you have responsibility!",
				getReply(npc));
		en.step(player, "blue");
		assertEquals(
				"Aw, don't be sad :( Put some nice music on, perhaps ... ", getReply(npc));
		en.step(player, "words");
		assertEquals(
				"Roses are red, violets are blue, Stendhal is great, and so are you!",
				getReply(npc));
		en.step(player, "portals");
		assertEquals(
				"The one with the Sun goes to semos city. It shows you where this house really is. The rest are clear, I hope. There is a door to the bank, the jail, and the Death Match in Ados. Of course they are all one way portals so you will not be disturbed by unexpected visitors.",
				getReply(npc));
		en.step(player, "quest");
		assertEquals(
				"Now you're really testing how much thought went into making me!",
				getReply(npc));
		player.setBaseHP(200);
		player.setHP(100);
		assertEquals(100, player.getHP());
		en.step(player, "heal");
		assertEquals("There, you are healed. How else may I help you?", getReply(npc));
		assertEquals(200, player.getHP());
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye, remember to take care of yourself.", getReply(npc));

	}

}
