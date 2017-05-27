/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class HandleTransitionsTest {

	static HandleTransitions script = new HandleTransitions();
	final static Logger logger = Logger.getLogger(HandleTransitionsTest.class);
	List<String> arguments = new LinkedList<String>();
	static Engine en = null;
	static SpeakerNPC npc = null;
	static Player player = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("int_semos_guard_house",100,100);
		SingletonRepository.getRPWorld().addRPZone(zone);
		npc = new SpeakerNPC("just_npc");

		zone.add(npc);
		npc.setPosition(1, 1);

		// simple transition
		npc.add(ConversationStates.IDLE, "test", null, ConversationStates.IDLE, "test", null);
		npc.add(ConversationStates.IDLE, "tr1", null, ConversationStates.IDLE, "tr1", null);
		npc.addGreeting();
		npc.addGoodbye();

		en = npc.getEngine();
		en.setCurrentState(ConversationStates.IDLE);

		/*
		 * creating player
		 */
		player = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(player);
		player.setAdminLevel(1000);
		zone.add(player);
		player.setPosition(2, 2);
	}

	/**
	 * Tests for -list command
	 */
	@Test
	public void testList() {
		player.clearEvents();
		arguments.clear();
		arguments.add("just_npc");
		arguments.add("-list");
		script.execute(player, arguments);
		assertThat(player.events().get(0).toString(), containsString("test"));
	}

	/**
	 * Tests for -add command
	 */
	@Test
	public void testAdd() {
		player.clearEvents();
		arguments.clear();
		arguments.add("just_npc");
		arguments.add("-add");
		arguments.add("added as tr1");
		arguments.add("tr1");
		arguments.add("i am just npc as you can see.");
		script.execute(player, arguments);
		assertThat(player.events().get(0).toString(), containsString("added"));
		en.step(player, "bye");
		en.step(player, "hi");
		getReply(npc);
		en.step(player, "tr1");
		assertEquals("i am just npc as you can see.", getReply(npc));
	}

	/**
	 * Tests for -del command
	 */
	@Test
	public void testDel() {
		npc.add(
				ConversationStates.ANY,
				"tr2",
				null,
				ConversationStates.ANY,
				"i am just npc as you can see.",
				null,
				"added as tr2"
				);
		en.step(player, "bye");
		en.step(player, "hi");
		getReply(npc);
		en.step(player, "tr2");
		assertEquals("i am just npc as you can see.", getReply(npc));

		player.clearEvents();
		arguments.clear();
		arguments.add("just_npc");
		arguments.add("-del");
		arguments.add("added as tr2");
		script.execute(player, arguments);
		//logger.info(player.events().toString());
		assertThat(player.events().get(0).toString(), containsString("deleted"));

		en.step(player, "bye");
		en.step(player, "hi");
		getReply(npc);
		en.step(player, "tr2");
		assertEquals(null, getReply(npc));
	}


	/**
	 * Tests for -alter command
	 */
	@Test
	public void testAlter() {
		player.clearEvents();
		npc.add(
				ConversationStates.ANY,
				"tr3",
				null,
				ConversationStates.ANY,
				"Its white.",
				null,
				"added as tr3"
				);
		en.step(player, "bye");
		en.step(player, "hi");
		getReply(npc);
		en.step(player, "tr3");
		assertEquals("Its white.", getReply(npc));

		player.clearEvents();
		arguments.clear();
		arguments.add("just_npc");
		arguments.add("-alter");
		arguments.add("added as tr3");
		arguments.add("tr3");
		arguments.add("Its black.");
		script.execute(player, arguments);

		player.clearEvents();
		en.step(player, "bye");
		en.step(player, "hi");
		getReply(npc);
		en.step(player, "tr3");
		assertEquals("Its black.", getReply(npc));
	}


}
