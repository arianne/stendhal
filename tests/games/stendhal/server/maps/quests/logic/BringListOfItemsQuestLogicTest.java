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
package games.stendhal.server.maps.quests.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class BringListOfItemsQuestLogicTest {
	@BeforeClass
	public static void setupClass() {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		PlayerTestHelper.generateNPCRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for bringListOfItemsQuestLogic.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testBringListOfItemsQuestLogic() {
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(new NullValueMockBringListOfItemsQuest());
		logic.addToWorld();
	}

	/**
	 * Tests for getListOfStillMissingItems.
	 */
	@Test
	public final void testGetListOfStillMissingItems() {
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(new NullValueMockBringListOfItemsQuest() {
			@Override
			public List<String> getNeededItems() {

				return new LinkedList<String>();
			}
		});
		assertEquals("emptyList", Arrays.asList(new String[] {}), logic.getListOfStillMissingItems(
				PlayerTestHelper.createPlayer("player"), false));
		final BringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest() {
			@Override
			public List<String> getNeededItems() {

				return Arrays.asList(new String[] { "one", "two", "three" });
			}
		};
		logic = new BringListOfItemsQuestLogic(quest);
		assertEquals(Arrays.asList(new String[] { "one", "two", "three" }), logic.getListOfStillMissingItems(
				PlayerTestHelper.createPlayer("player"), false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two", "#three" }), logic.getListOfStillMissingItems(
				PlayerTestHelper.createPlayer("player"), true));

		final Player bob = PlayerTestHelper.createPlayer("player");
		bob.setQuest(quest.getSlotName(), "");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "two", "three" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two", "#three" }), logic.getListOfStillMissingItems(bob,
				true));
		bob.setQuest(quest.getSlotName(), "one");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "two", "three" }), logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#two", "#three" }), logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "two");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "three" }), logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#three" }), logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "three");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "two" }), logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two" }), logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "three;two");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one" }), logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one" }), logic.getListOfStillMissingItems(bob, true));
	}

	/**
	 * Tests for welcomeNewPlayer.
	 */
	@Test
	public final void testWelcomeNewPlayer() {
		final NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest();
		final SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomeNewPlayer();

		final Player player = PlayerTestHelper.createPlayer("player");
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(quest.welcomeBeforeStartingQuest(), getReply(npc));
	}

	/**
	 * Tests for shouldNotWelcomePlayerAfterQuest.
	 */
	@Test
	public final void testShouldNotWelcomePlayerAfterQuest() {
		final NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest();
		final SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomePlayerAfterQuest();

		final Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest(quest.getSlotName(), "done");
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertFalse(npc.isTalking());
	}

	/**
	 * Tests for shouldWelcomePlayerAfterQuest.
	 */
	@Test
	public final void testShouldWelcomePlayerAfterQuest() {
		final NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest() {
			@Override
			public boolean shouldWelcomeAfterQuestIsCompleted() {
				return true;
			}
		};
		final SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomePlayerAfterQuest();

		final Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest(quest.getSlotName(), "done");
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(quest.welcomeAfterQuestIsCompleted(), getReply(npc));
	}

	@Test
	public final void doQuest() {
		final MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		final SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		final Player player = PlayerTestHelper.createPlayer("player");
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), getReply(npc));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), getReply(npc));

		en.step(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("answer to quests accepted", quest.respondToQuestAcception(), getReply(npc));
		assertTrue(player.hasQuest(quest.getSlotName()));
		assertFalse(npc.isTalking());
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());

		en.step(player, quest.getTriggerPhraseToEnumerateMissingItems().get(0));
		assertEquals("i have not brought anything yet it should be all needed items",
				"#one, #two, and #three", getReply(npc));

		StackableItem item = new StackableItem("one", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		en.step(player, "yes");
		assertEquals("item brought", quest.askForItemsAfterPlayerSaidHeHasItems(), getReply(npc));

		en.step(player, "one");
		assertEquals("item brought", quest.respondToItemBrought(), getReply(npc));
		en.step(player, "one");
		assertEquals("item brought", quest.respondToOfferOfNotMissingItem(), getReply(npc));
		npc.remove("text");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		en.step(player, quest.getTriggerPhraseToEnumerateMissingItems().get(0));
		final List<String> missing = new LinkedList<String>(quest.getNeededItems());
		missing.remove("one");
		assertEquals("two and three are missing", "#two and #three", getReply(npc));
		en.step(player, "two");
		assertEquals("item brought", quest.respondToOfferOfNotExistingItem("two"), getReply(npc));

		item = new StackableItem("two", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		item = new StackableItem("three", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		en.step(player, "three");
		assertEquals("item brought", quest.respondToItemBrought(), getReply(npc));
		en.step(player, "two");
		assertEquals("last item brought", quest.respondToLastItemBrought(), getReply(npc));
	}

	/**
	 * Tests for acceptQuest.
	 */
	@Test
	public final void testAcceptQuest() {
		final MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		final SpeakerNPC npc = new SpeakerNPC("npcAccept");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		final Player player = PlayerTestHelper.createPlayer("player");

		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), getReply(npc));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), getReply(npc));

		en.step(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("answer to quests accepted", quest.respondToQuestAcception(), getReply(npc));
	}

	/**
	 * Tests for rejectQuest.
	 */
	@Test
	public final void testRejectQuest() {
		final MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		final SpeakerNPC npc = new SpeakerNPC("npcReject");
		quest.setNpc(npc);
		final BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		final Player player = PlayerTestHelper.createPlayer("player");
		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), getReply(npc));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), getReply(npc));

		en.step(player, "no");
		assertEquals("answer to quests accepted", quest.respondToQuestRefusal(), getReply(npc));
	}

	class MockBringListOfItemsQuest implements BringListOfItemsQuest {
		private SpeakerNPC npc;

		private boolean isWelcomingAfterQuests;

		// makes our live easier
		public void setNpc(final SpeakerNPC npc) {
			this.npc = npc;
		}

		@Override
		public String askForItemsAfterPlayerSaidHeHasItems() {
			return "askForItemsAfterPlayerSaidHeHasItems";
		}

		@Override
		public String askForMissingItems(final List<String> missingItems) {
			return Grammar.enumerateCollection(missingItems);
		}

		@Override
		public String firstAskForMissingItems(final List<String> missingItems) {
			return "firstAskForMissingItems";
		}

		@Override
		public List<String> getAdditionalTriggerPhraseForQuest() {
			return Arrays.asList(new String[] { "getAdditionalTriggerPhraseForQuest" });
		}

		@Override
		public SpeakerNPC getNPC() {
			if (npc == null) {

				PlayerTestHelper.generateNPCRPClasses();
				npc = new SpeakerNPC("MockBringListOfItemsQuest");
			}
			return npc;
		}

		@Override
		public List<String> getNeededItems() {
			return Arrays.asList(new String[] { "one", "two", "three" });
		}

		@Override
		public String getSlotName() {
			return "MockBringListOfItemsQuest";
		}

		@Override
		public List<String> getTriggerPhraseToEnumerateMissingItems() {
			return Arrays.asList("getTriggerPhraseToEnumerateMissingItems");
		}

		@Override
		public String respondToItemBrought() {
			return "respondToItemBrought";
		}

		@Override
		public String respondToLastItemBrought() {
			return "respondToLastItemBrought";
		}

		@Override
		public String respondToOfferOfNotExistingItem(final String itemName) {
			return "respondToOfferOfNotExistingItem" + itemName;
		}

		@Override
		public String respondToOfferOfNotMissingItem() {
			return "respondToOfferOfNotMissingItem";
		}

		@Override
		public String respondToOfferOfNotNeededItem() {
			return "respondToOfferOfNotNeededItem";
		}

		@Override
		public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
			return "respondToPlayerSayingHeHasNoItems";
		}

		@Override
		public String respondToQuest() {
			return "respondToQuest";
		}

		@Override
		public String respondToQuestAcception() {
			return "respondToQuestAcception";
		}

		@Override
		public String respondToQuestAfterItHasAlreadyBeenCompleted() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		@Override
		public String respondToQuestRefusal() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		@Override
		public void rewardPlayer(final Player player) {

		}

		@Override
		public boolean shouldWelcomeAfterQuestIsCompleted() {
			return isWelcomingAfterQuests;
		}

		@Override
		public String welcomeAfterQuestIsCompleted() {
			return "shouldWelcomeAfterQuestIsCompleted";
		}

		@Override
		public String welcomeBeforeStartingQuest() {
			return "welcomeBeforeStartingQuest";
		}

		@Override
		public String welcomeDuringActiveQuest() {
			return "welcomeDuringActiveQuest";
		}

		void setWelcomingAfterQuests(final boolean isWelcomingAfterQuests) {
			this.isWelcomingAfterQuests = isWelcomingAfterQuests;
		}

		@Override
		public double getKarmaDiffForQuestResponse() {
			return 5.0;
		}
	}

	/**
	 * returns null for everything except name.
	 *
	 * @author astridemma
	 *
	 */
	class NullValueMockBringListOfItemsQuest implements BringListOfItemsQuest {
		private SpeakerNPC npc;

		@Override
		public String askForItemsAfterPlayerSaidHeHasItems() {
			return null;
		}

		@Override
		public String askForMissingItems(final List<String> missingItems) {
			return null;
		}

		@Override
		public String firstAskForMissingItems(final List<String> missingItems) {
			return null;
		}

		@Override
		public List<String> getAdditionalTriggerPhraseForQuest() {
			return null;
		}

		@Override
		public SpeakerNPC getNPC() {
			if (npc == null) {

				PlayerTestHelper.generateNPCRPClasses();
				npc = new SpeakerNPC("MockBringListOfItemsQuest");
			}
			return npc;
		}

		@Override
		public List<String> getNeededItems() {
			return null;
		}

		@Override
		public String getSlotName() {

			return "NullValueMockBringListOfItemsQuest";
		}

		@Override
		public List<String> getTriggerPhraseToEnumerateMissingItems() {
			return null;
		}

		@Override
		public String respondToItemBrought() {

			return null;
		}

		@Override
		public String respondToLastItemBrought() {

			return null;
		}

		@Override
		public String respondToOfferOfNotExistingItem(final String itemName) {

			return null;
		}

		@Override
		public String respondToOfferOfNotMissingItem() {

			return null;
		}

		@Override
		public String respondToOfferOfNotNeededItem() {
			return null;
		}

		@Override
		public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
			return null;
		}

		@Override
		public String respondToQuest() {
			return "respondToQuest";
		}

		@Override
		public String respondToQuestAcception() {
			return "respondToQuestAcception";
		}

		@Override
		public String respondToQuestAfterItHasAlreadyBeenCompleted() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		@Override
		public String respondToQuestRefusal() {
			return "respondToQuestRefusal";
		}

		@Override
		public void rewardPlayer(final Player player) {

		}

		@Override
		public boolean shouldWelcomeAfterQuestIsCompleted() {
			return false;
		}

		@Override
		public String welcomeAfterQuestIsCompleted() {
			return "welcomeAfterQuestIsCompleted";
		}

		@Override
		public String welcomeBeforeStartingQuest() {
			return "welcomeBeforeStartingQuest";
		}

		@Override
		public String welcomeDuringActiveQuest() {
			return "welcomeDuringActiveQuest";
		}

		public void setNpc(final SpeakerNPC npc) {
			this.npc = npc;
		}

		@Override
		public double getKarmaDiffForQuestResponse() {
			return 0;
		}
	}
}
