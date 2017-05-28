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
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class WeaponsCollector2Test {
	@BeforeClass
	public static void setupclass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("Balduin");
	}

	/**
	 * Tests for getSlotName.
	 */
	@Test
	public final void testGetSlotName() {
		final WeaponsCollector2 wc = new WeaponsCollector2();
		assertEquals("weapons_collector2", wc.getSlotName());
	}

	@Test
	public final void rejectQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Balduin"));
		final WeaponsCollector2 wc = new WeaponsCollector2();
		wc.addToWorld();
		final SpeakerNPC npc = wc.getNPC();
		final Engine en = npc.getEngine();
		final Player pl = PlayerTestHelper.createPlayer("player");

		// set previous quest to done
		pl.setQuest("weapons_collector", "done");

		assertTrue(en.stepTest(pl, ConversationPhrases.GREETING_MESSAGES.get(0)));
		assertEquals(
				"Greetings, old friend. If you are willing, I have another #quest for you.",
				getReply(npc));

		assertTrue(en.stepTest(pl, ConversationPhrases.QUEST_MESSAGES.get(0)));
		assertEquals(
				"Recent adventurers to these parts describe strange new creatures with weapons"
						+ " I have never seen. Would you fight these creatures and bring their weapons to me?",
				getReply(npc));
		en.stepTest(pl, "no");
		assertEquals("Well, maybe someone else will happen by and help me.",
				getReply(npc));
	}

	@Test
	public final void doQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Balduin"));
		final WeaponsCollector2 wc = new WeaponsCollector2();

		wc.addToWorld();
		final SpeakerNPC npc = wc.getNPC();
		final Engine en = npc.getEngine();
		final Player pl = PlayerTestHelper.createPlayer("player");

		// set previous quest to done
		pl.setQuest("weapons_collector", "done");

		assertTrue(en.stepTest(pl, "hello"));
		assertEquals(
				"Greetings, old friend. If you are willing, I have another #quest for you.",
				getReply(npc));

		assertTrue(en.stepTest(pl, "quest"));
		assertEquals(
				"Recent adventurers to these parts describe strange new creatures with weapons"
						+ " I have never seen. Would you fight these creatures and bring their weapons to me?",
				getReply(npc));

		assertTrue(en.stepTest(pl, ConversationPhrases.YES_MESSAGES.get(0)));
		assertEquals(
				"Wonderful. Now, the #list is small but the risk may be great. If you return safely, I have another reward for you.",
				getReply(npc));

		assertTrue(en.stepTest(pl, "list"));
		assertEquals(
				"There are 3 weapons still missing from my newest collection: #'morning star', #staff, and #'great sword'."
						+ " Do you have anything like that with you?",
				getReply(npc));

		assertTrue(en.stepTest(pl, "no"));
		assertEquals("Let me know as soon as you find them. Farewell.",
				getReply(npc));

		// start another conversation
		assertTrue(en.stepTest(pl, "hi"));
		assertEquals(
				"Welcome back. I hope you have come to help me with my latest #list of weapons.",
				getReply(npc));

		assertTrue(en.stepTest(pl, "list"));
		assertEquals(
				"There are 3 weapons still missing from my newest collection: #'morning star', #staff, and #'great sword'."
						+ " Do you have anything like that with you?",
				getReply(npc));

		assertTrue(en.stepTest(pl, "yes"));
		assertEquals("What did you find?", getReply(npc));

		Item weapon = new Item("morning star", "", "", null);
		pl.getSlot("bag").add(weapon);

		assertTrue(en.stepTest(pl, "morning star"));
		assertEquals("Thank you very much! Do you have anything more for me?",
				getReply(npc));

		assertTrue(en.stepTest(pl, "morning star"));
		assertEquals(
				"I already have that one. Do you have any other weapon for me?",
				getReply(npc));

		for (final String cloakName : wc.getNeededItems()) {
			weapon = new Item(cloakName, "", "", null);
			pl.getSlot("bag").add(weapon);
			en.step(pl, cloakName);
		}

		assertEquals(
				"At last, my collection is complete! Thank you very much; here, take this pair of swords in exchange!",
				getReply(npc));
		en.step(pl, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		assertTrue(wc.isCompleted(pl));
	}
}
