/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReplies;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class LuaTest extends ZonePlayerAndNPCTestImpl {

	private static final Logger logger = Logger.getLogger(LuaTest.class);

	private static StendhalRPZone zone;
	private static SpeakerNPC npc;
	private Engine en;
	private static final String npcName = "Lua";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		zone = setupZone("0_semos_city");
		// initialize Lua globals
		ScriptInLua.get().init();
		new ScriptRunner().perform("npc/example/init.lua", true);

		npc = SingletonRepository.getNPCList().get(npcName);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setNpcNames(npcName);
		setZoneForPlayer(zone.getName());

		super.setUp();
	}

	@Test
	public void runTests() {
		if (npc == null) {
			logger.info("Lua example disabled, not running Lua tests");
			return;
		}

		// stop entity's movement
		npc.stop();
		en = npc.getEngine();

		testEntities();
		testDialogue();
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(npc);

		// NPC position
		assertEquals(10, npc.getX());
		assertEquals(55, npc.getY());

		// player position
		assertEquals(0, player.getX());
		assertEquals(0, player.getY());
	}

	private void testDialogue() {
		assertTrue(en.step(player, "hi"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I am sad, because I do not have a job.", getReply(npc));
		assertTrue(en.step(player, "job"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Actually, I am jobless.", getReply(npc));

		// NPC has multiple events for the following responses
		List<String> replies;

		// player not standing next to NPC
		assertTrue(en.step(player, "Lua"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());

		replies = getReplies(npc);
		assertEquals(2, replies.size());

		assertEquals("That's my name, don't wear it out!", replies.get(0));
		assertEquals("!me giggles", replies.get(1));

		// reset replies list
		replies.clear();
		assertEquals(0, replies.size());

		// player standing next to NPC
		player.setPosition(npc.getX() - 1, npc.getY());
		assertTrue(player.nextTo(npc));

		assertTrue(en.step(player, "Lua"));

		replies = getReplies(npc);
		assertEquals(2, replies.size());

		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Um, could you back up please? I can smell your breath.", replies.get(0));
		assertEquals("!me coughs", replies.get(1));

		assertTrue(en.step(player, "bye"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Bye.", getReply(npc));
	}
}
