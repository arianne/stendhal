/***************************************************************************
 *                    Copyright © 2019-2023 - Stendhal                     *
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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class LuaTest extends ZonePlayerAndNPCTestImpl {

	private SpeakerNPC lua;

	private static final String propKey = "stendhal.testserver";
	private static String propValue;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		propValue = System.getProperty(propKey);
		System.setProperty(propKey, "junk");
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (propValue == null) {
			System.clearProperty(propKey);
		} else {
			System.setProperty(propKey, propValue);
		}
	}

	@Override
	@Before
	public void setUp() throws Exception {
		// load zone, NPC, & Player
		setZoneForPlayer(setupZone("0_semos_city").getName());
		assertNotNull(zone);
		assertEquals("0_semos_city", zone.getName());
		// check that zone was added to world
		assertNotNull(SingletonRepository.getRPWorld().getZone("0_semos_city"));
		new ScriptRunner().perform("region/semos/city/ExampleNPC.lua");
		assertEquals(1, zone.getNPCList().size());
		// FIXME: zone doesn't exist in world when ZonePlayerAndNPCTestImpl.tearDown is called
		setNpcNames("Lua");

		super.setUp();

		lua = SingletonRepository.getNPCList().get("Lua");
		assertNotNull(lua);
		assertEquals("0_semos_city", lua.getZone().getName());
		assertEquals(10, lua.getX());
		assertEquals(55, lua.getY());

		assertNotNull(player);
		assertEquals("0_semos_city", player.getZone().getName());
		assertEquals(0, player.getX());
		assertEquals(0, player.getY());
	}

	@Test
	public void testDialogue() {
		final Engine en = lua.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hi there!", getReply(lua));
		assertTrue(en.step(player, "job"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I am an example of how to create an entity using the Lua scripting interface.", getReply(lua));
		assertTrue(en.step(player, "help"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("How can I help you? I am just a kid.", getReply(lua));
		assertTrue(en.step(player, "offer"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I have a small #task you could help me with.", getReply(lua));

		// NPC has multiple events for the following responses
		List<String> replies;

		// player not standing next to NPC
		assertTrue(en.step(player, "Lua"));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());

		replies = getReplies(lua);
		assertEquals(2, replies.size());

		assertEquals("That's my name, don't wear it out!", replies.get(0));
		assertEquals("!me giggles", replies.get(1));

		// reset replies list
		replies.clear();
		assertEquals(0, replies.size());

		// player standing next to NPC
		player.setPosition(lua.getX() - 1, lua.getY());
		assertTrue(player.nextTo(lua));

		assertTrue(en.step(player, "Lua"));

		replies = getReplies(lua);
		assertEquals(2, replies.size());

		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Um, could you back up please? I can smell your breath.", replies.get(0));
		assertEquals("!me coughs", replies.get(1));

		assertTrue(en.step(player, "bye"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Buh bye!", getReply(lua));
	}
}
