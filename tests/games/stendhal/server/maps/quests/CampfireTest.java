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

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.orril.river.CampingGirlNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;
import utilities.PlayerTestHelper;

public class CampfireTest {

	private static final String ZONE_NAME = "testzone";

	private static final String CAMPFIRE = "campfire";

	private Player player;

	private SpeakerNPC npc;

	private StendhalRPZone zone;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		zone = new StendhalRPZone("zone");
		new CampingGirlNPC().configureZone(zone, null);
		npc = NPCList.get().get("Sally");
		final Campfire cf = new Campfire();
		cf.addToWorld();
	}

	@After
	public void tearDown()  {
		player = null;
		NPCList.get().clear();
	}

	/**
	 * Tests for canStartQuestNow.
	 */
	@Test
	public void testCanStartQuestNow() {

		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! I need a little #favor ... ", getReply(npc));
		assertTrue(en.step(player, "bye"));

		player.setQuest(CampfireTest.CAMPFIRE, "start");
		assertTrue(en.step(player, "hi"));
		assertEquals(
				"You're back already? Don't forget that you promised to collect ten pieces of wood for me!",
				getReply(npc));
		assertTrue(en.step(player, "bye"));

		player.setQuest(CampfireTest.CAMPFIRE, String.valueOf(System.currentTimeMillis()));
		en.step(player, "hi");
		assertEquals(
				"Hi again!",
				getReply(npc));
		assertTrue(en.step(player, "bye"));

		final long SIXMINUTESAGO = System.currentTimeMillis() - 6 * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
		player.setQuest(CampfireTest.CAMPFIRE, String.valueOf(SIXMINUTESAGO));
		en.step(player, "hi");
		assertEquals("delay is 5 minutes, so 6 minutes should be enough", "Hi again!", getReply(npc));
		assertTrue(en.step(player, "bye"));
	}

	/**
	 * Tests for hiAndbye.
	 */
	@Test
	public void testHiAndbye() {

		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! I need a little #favor ... ", getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for doQuest.
	 */
	@Test
	public void testDoQuest() {

		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! I need a little #favor ... ", getReply(npc));
		assertTrue(en.step(player, "favor"));

		assertEquals(
				"I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.",
				getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals(
				"Okay. You can find wood in the forest north of here. Come back when you get ten pieces of wood!",
				getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
		final StackableItem wood = new StackableItem("wood", "", "", null);
		wood.setQuantity(10);
		wood.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(wood);
		assertEquals(10, player.getNumberOfEquipped("wood"));
		assertTrue(en.step(player, "hi"));
		assertEquals(
				"Hi again! You've got wood, I see; do you have those 10 pieces of wood I asked about earlier?",
				getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals(0, player.getNumberOfEquipped("wood"));
		String reply = getReply(npc);
		assertTrue("Thank you! Here, take some meat and charcoal!".equals(reply)
				|| "Thank you! Here, take some ham and charcoal!".equals(reply));
		assertTrue((10 == player.getNumberOfEquipped("meat"))
				|| (10 == player.getNumberOfEquipped("ham")));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));

	}

	/**
	 * Tests for isRepeatable.
	 */
	@Test
	public void testIsRepeatable() {
		player.setQuest(CAMPFIRE, "start");
		assertFalse(new Campfire().isRepeatable(player));
		player.setQuest(CAMPFIRE, "rejected");
		assertFalse(new Campfire().isRepeatable(player));
		player.setQuest(CAMPFIRE, System.currentTimeMillis() + "");
		assertFalse(new Campfire().isRepeatable(player));
		player.setQuest(CAMPFIRE, "1");
		assertTrue(new Campfire().isRepeatable(player));
	}

	/**
	 * Tests for isCompleted.
	 */
	@Test
	public void testIsCompleted() {
		assertFalse(new Campfire().isCompleted(player));

		player.setQuest(CAMPFIRE, "start");
		assertFalse(new Campfire().isCompleted(player));

		player.setQuest(CAMPFIRE, "rejected");
		assertFalse(new Campfire().isCompleted(player));

		player.setQuest(CAMPFIRE, "notStartorRejected");
		assertTrue(new Campfire().isCompleted(player));
	}

	/**
	 * Tests for jobAndOffer.
	 */
	@Test
	public void testJobAndOffer() {
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! I need a little #favor ... ", getReply(npc));
		assertTrue(en.step(player, "job"));
		assertEquals("Work? I'm just a little girl! I'm a scout, you know.",
				getReply(npc));
		assertFalse("no matching state transition", en.step(player, "offers"));
		assertEquals(null, getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals(
				"You can find lots of useful stuff in the forest; wood and mushrooms, for example. But beware, some mushrooms are poisonous!",
				getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for canNotRepeatYet.
	 */
	@Test
	public void testCanNotRepeatYet() {
		final String questState = Long.toString(new Date().getTime());

		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Engine en = npc.getEngine();
			player.setQuest(CAMPFIRE, questState);

			en.setCurrentState(ConversationStates.ATTENDING);
			en.step(player, request);
			String reply = getReply(npc);
			assertTrue("Thanks, but I think the wood you brought already will last me 60 minutes.".equals(reply) || "Thanks, but I think the wood you brought already will last me 1 hour.".equals(reply));
			assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
			assertEquals("quest state unchanged", questState, player.getQuest(CAMPFIRE));
		}
	}

	/**
	 * Tests for repeatQuest.
	 */
	@Test
	public void testRepeatQuest() {
		final String questState = Long.toString(new Date().getTime() - 61 * 60 * 1000);
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Engine en = npc.getEngine();
			player.setQuest(CAMPFIRE, questState);

			en.setCurrentState(ConversationStates.ATTENDING);
			en.step(player, request);
			assertEquals("My campfire needs wood again, ten pieces of #wood will be enough. Could you please get those #wood pieces from the forest for me? Please say yes!", getReply(npc));
			assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
			assertEquals("quest state unchanged", questState, player.getQuest(CAMPFIRE));
		}
	}

	/**
	 * Tests for allowRestartAfterRejecting.
	 */
	@Test
	public void testAllowRestartAfterRejecting() {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Engine en = npc.getEngine();
			player.setQuest(CAMPFIRE, "rejected");

			en.setCurrentState(ConversationStates.ATTENDING);
			en.step(player, request);
			assertEquals("I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.", getReply(npc));
			assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
			assertEquals("quest state unchanged", "rejected", player.getQuest(CAMPFIRE));
		}
	}

	/**
	 * Tests for refuseQuest.
	 */
	@Test
	public void testRefuseQuest() {
		final Engine en = npc.getEngine();
		final double karma = player.getKarma();

		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "no");

		assertEquals("Oh dear, how am I going to cook all this meat? Perhaps I'll just have to feed it to the animals..."
, getReply(npc));
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("quest state 'rejected'", "rejected", player.getQuest(CAMPFIRE));
		assertEquals("karma penalty", karma - 5.0, player.getKarma(), 0.01);
	}
}
