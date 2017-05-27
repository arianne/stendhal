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

public class WeaponsCollectorTest {
	@BeforeClass
	public static void setupclass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		PlayerTestHelper.removeNPC("Balduin");
	}

	/**
	 * Tests for getSlotName.
	 */
	@Test
	public final void testGetSlotName() {
		final WeaponsCollector wc = new WeaponsCollector();
		assertEquals("weapons_collector", wc.getSlotName());
	}

	@Test
	public final void rejectQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Balduin"));
		final WeaponsCollector wc = new WeaponsCollector();

		wc.addToWorld();
		final SpeakerNPC npc = wc.getNPC();
		final Engine en = npc.getEngine();
		final Player pl = PlayerTestHelper.createPlayer("player");

		assertTrue(en.stepTest(pl, "hi"));
		assertEquals(wc.welcomeBeforeStartingQuest(), getReply(npc));

		assertTrue(en.stepTest(pl, "quest"));
		assertEquals(wc.respondToQuest(), getReply(npc));

		assertTrue(en.stepTest(pl, "no"));
		assertEquals(wc.respondToQuestRefusal(), getReply(npc));
	}

	@Test
	public final void doQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Balduin"));
		final WeaponsCollector wc = new WeaponsCollector();

		wc.addToWorld();
		final SpeakerNPC npc = wc.getNPC();
		final Engine en = npc.getEngine();
		final Player pl = PlayerTestHelper.createPlayer("pl");

		assertTrue(en.stepTest(pl, "hello"));
		assertEquals(wc.welcomeBeforeStartingQuest(), getReply(npc));

		assertTrue(en.stepTest(pl, "quest"));
		assertEquals(wc.respondToQuest(), getReply(npc));

		assertTrue(en.stepTest(pl, "collection"));
		assertEquals(wc.firstAskForMissingItems(wc.getNeededItems()), getReply(npc));

		assertTrue(en.stepTest(pl, "yes"));
		assertEquals(wc.respondToQuestAcception(), getReply(npc));

		//npc has stopped conversation

		assertTrue("the quest was accepted, so it should be started", wc.isStarted(pl));
		assertFalse(wc.isCompleted(pl));

		assertTrue(en.stepTest(pl, ConversationPhrases.GREETING_MESSAGES.get(0)));
		assertEquals(wc.welcomeDuringActiveQuest(), getReply(npc));
		npc.remove("text");
		en.stepTest(pl, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(wc.askForItemsAfterPlayerSaidHeHasItems(), getReply(npc));

		en.stepTest(pl, "bardiche");
		assertEquals(wc.respondToOfferOfNotExistingItem("bardiche"), getReply(npc));

        Item cloak = new Item("bardiche", "", "", null);
		pl.getSlot("bag").add(cloak);

		assertTrue(en.stepTest(pl, "bardiche"));
		assertEquals(wc.respondToItemBrought(), getReply(npc));

		assertTrue(en.stepTest(pl, "bardiche"));
		assertEquals(wc.respondToOfferOfNotMissingItem(), getReply(npc));

		cloak = new Item("stone cloak", "", "", null);
		pl.getSlot("bag").add(cloak);

		for (final String cloakName : wc.getNeededItems()) {
			cloak = new Item(cloakName, "", "", null);
			pl.getSlot("bag").add(cloak);
			assertTrue(en.step(pl, cloakName));
		}

		assertEquals(wc.respondToLastItemBrought(), getReply(npc));
		assertTrue(wc.isCompleted(pl));
		assertTrue(npc.isTalking());
	}

	/**
	 * Tests for shouldWelcomeAfterQuestIsCompleted.
	 */
	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		final WeaponsCollector wc = new WeaponsCollector();
		assertFalse(wc.shouldWelcomeAfterQuestIsCompleted());
	}

	/**
	 * Tests for rewardPlayer.
	 */
	@Test
	public final void testRewardPlayer() {
		final WeaponsCollector wc = new WeaponsCollector();
		final Player player = PlayerTestHelper.createPlayer("player");
		final int oldXP = player.getXP();
		wc.rewardPlayer(player);

		assertTrue(player.isEquipped("ice sword"));
		assertEquals(oldXP + 5000, player.getXP());
	}
}
