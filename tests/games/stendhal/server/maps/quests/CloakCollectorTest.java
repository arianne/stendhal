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

public class CloakCollectorTest {
	@BeforeClass
	public static void setupclass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("Josephine");
	}

	@Test
	public final void rejectQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Josephine"));
		final CloakCollector cc = new CloakCollector();
		cc.addToWorld();
		final SpeakerNPC npc = cc.getNPC();
		final Engine en = npc.getEngine();
		final Player monica = PlayerTestHelper.createPlayer("player");

		en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(cc.welcomeBeforeStartingQuest(), getReply(npc));

		en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
		assertEquals(cc.respondToQuest(), getReply(npc));

		en.stepTest(monica, cc.getTriggerPhraseToEnumerateMissingItems().get(0));
		assertEquals(cc.firstAskForMissingItems(cc.getNeededItems()), getReply(npc));

		en.stepTest(monica, "no");
		assertEquals(cc.respondToQuestRefusal(), getReply(npc));
	}

	@Test
	public final void doQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Josephine"));
		final CloakCollector cc = new CloakCollector();
		cc.addToWorld();

		final SpeakerNPC npc = cc.getNPC();
		final Engine en = npc.getEngine();
		final Player monica = PlayerTestHelper.createPlayer("monica");

		en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(cc.welcomeBeforeStartingQuest(), getReply(npc));

		en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
		assertEquals(cc.respondToQuest(), getReply(npc));

		en.stepTest(monica, "elf cloak");
		assertEquals(
				"You haven't seen one before? Well, it's a elf cloak. So, will you find them all?",
				getReply(npc));

		en.stepTest(monica, "pink cloak");
		assertEquals("I don't know pink cloak. Can you name me another cloak please?", getReply(npc));

		en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(cc.respondToQuestAcception(), getReply(npc));
		assertFalse(npc.isTalking());
		npc.remove("text");

		assertTrue("the quest was accepted, so it should be started",
				cc.isStarted(monica));
		assertFalse(cc.isCompleted(monica));

		en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(cc.welcomeDuringActiveQuest(), getReply(npc));
		npc.remove("text");
		en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(cc.askForItemsAfterPlayerSaidHeHasItems(), getReply(npc));

		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToOfferOfNotExistingItem("elf cloak"),
				getReply(npc));

		Item cloak = new Item("elf cloak", "", "", null);
		monica.getSlot("bag").add(cloak);
		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToItemBrought(), getReply(npc));
		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToOfferOfNotMissingItem(), getReply(npc));

		cloak = new Item("stone cloak", "", "", null);
		monica.getSlot("bag").add(cloak);

		for (final String cloakName : cc.getNeededItems()) {
			cloak = new Item(cloakName, "", "", null);
			monica.getSlot("bag").add(cloak);
			en.step(monica, cloakName);
		}

		assertEquals(cc.respondToLastItemBrought(), getReply(npc));
		en.step(monica, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertTrue(cc.isCompleted(monica));
	}

	/**
	 * Tests for getSlotName.
	 */
	@Test
	public final void testGetSlotName() {
		final CloakCollector cc = new CloakCollector();
		assertEquals("cloaks_collector", cc.getSlotName());
	}

	/**
	 * Tests for shouldWelcomeAfterQuestIsCompleted.
	 */
	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		final CloakCollector cc = new CloakCollector();
		assertFalse(cc.shouldWelcomeAfterQuestIsCompleted());
	}

	/**
	 * Tests for rewardPlayer.
	 */
	@Test
	public final void testRewardPlayer() {
		final CloakCollector cc = new CloakCollector();
		final Player player = PlayerTestHelper.createPlayer("player");
		final double oldKarma = player.getKarma();
		cc.rewardPlayer(player);
		assertTrue(player.isEquipped("black cloak"));
		assertEquals(oldKarma + 5.0, player.getKarma(), 0.01);
		assertEquals(10000, player.getXP());
	}

}
