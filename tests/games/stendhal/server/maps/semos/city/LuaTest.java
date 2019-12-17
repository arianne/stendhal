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
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class LuaTest extends ZonePlayerAndNPCTestImpl {

	private static final Logger logger = Logger.getLogger(LuaTest.class);

	private SpeakerNPC npc;
	private Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		setupZone("0_semos_city");
		new ScriptRunner().init();
	}

	@Override
	@Before
	public void setUp() {
		npc = SingletonRepository.getNPCList().get("Lua");
	}

	@Test
	public void runTests() {
		if (npc == null) {
			logger.info("Lua example disabled, not running Lua tests");
			return;
		}

		en = npc.getEngine();

		testDialogue();
	}

	public void testDialogue() {
		assertTrue(en.step(player, "hi"));
		assertEquals("I am sad, because I do not have a job.", getReply(npc));
		assertTrue(en.step(player, "job"));
		assertEquals("Actually, I am jobless.", getReply(npc));
	}
}
