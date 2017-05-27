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
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test for the MeetKetteh quest
 */
public class MeetKettehTest {
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private AbstractQuest quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new DecencyAndMannersWardenNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Ketteh Wehoh");

		en = npc.getEngine();

		quest = new MeetKetteh();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("Jeeves");

		StendhalRPZone jailZone = new StendhalRPZone("test_jail", 100, 100);
		MockStendlRPWorld.get().addRPZone(jailZone);
		new Jail().configureZone(jailZone, null);
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-3_semos_jail", 100, 100));
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT! If you don't I'll call the guards!", getReply(npc));
		assertTrue(player.getQuest(quest.getSlotName()).startsWith("seen_naked"));

		en.step(player, "no");
		assertEquals("If you don't put on some clothes and leave, I shall scream for the guards!", getReply(npc));
		assertEquals("Ketteh won't talk to players who refuse put on clothes", ConversationStates.IDLE, en.getCurrentState());

		// Try to talk again, still naked
		// need to set up jail here
		en.step(player, "hi");
		assertEquals("Ugh, you STILL haven't put any clothes on. To jail for you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// Put on some clothes, and go greet her
		player.setOutfit(Outfit.getRandomOutfit());
		en.step(player, "hi");
		assertEquals("Hi again, Jeeves. I'm so glad you have some clothes on now. Now we can continue with the lesson in #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// Check coming back with different conditions after an initial shock
		player.setQuest(quest.getSlotName(), "seen_naked");
		// Put on some clothes, and go greet her
		en.step(player, "hi");
		assertEquals("Hi again, Jeeves. I'm so glad you have some clothes on now. Now we can continue with the lesson in #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// Try clothed player with a fresh quest state
		player.removeQuest(quest.getSlotName());
		en.step(player, "hi");
		assertEquals("Hi Jeeves, nice to meet you. You know, we have something in common - good #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners.", getReply(npc));
		en.step(player, "blue");
		assertEquals("Oh, aren't you the clever one!", getReply(npc));
		en.step(player, "manners");
		assertEquals("If you happen to talk to any of the other citizens, you should always begin the conversation saying \"hi\". People here are quite predictable and will always enjoy talking about their \"job\", they will respond if you ask for \"help\" and if you want to do a \"task\" for them, just say it. If they look like the trading type, you can ask for their \"offers\". To end the conversation, just say \"bye\".", getReply(npc));
		assertEquals("learnt_manners", player.getQuest(quest.getSlotName()));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
