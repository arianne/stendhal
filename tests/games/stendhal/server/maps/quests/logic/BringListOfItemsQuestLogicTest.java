package games.stendhal.server.maps.quests.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Grammar;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class BringListOfItemsQuestLogicTest {
	@BeforeClass
	public static void setupClass() {
		Log4J.init();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
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

	@Test(expected = IllegalArgumentException.class)
	public final void testBringListOfItemsQuestLogic() {
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(new NullValueMockBringListOfItemsQuest());
		logic.addToWorld();
	}

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
		BringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest() {
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

		Player bob = PlayerTestHelper.createPlayer("player");
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

	@Test
	public final void testWelcomeNewPlayer() {
		NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest();
		SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomeNewPlayer();

		Player player = PlayerTestHelper.createPlayer("player");
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(quest.welcomeBeforeStartingQuest(), npc.get("text"));
	}

	@Test
	public final void testShouldNotWelcomePlayerAfterQuest() {
		NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest();
		SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomePlayerAfterQuest();

		Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest(quest.getSlotName(), "done");
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertFalse(npc.isTalking());
	}

	@Test
	public final void testShouldWelcomePlayerAfterQuest() {
		NullValueMockBringListOfItemsQuest quest = new NullValueMockBringListOfItemsQuest() {
			@Override
			public boolean shouldWelcomeAfterQuestIsCompleted() {
				return true;
			}
		};
		SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomePlayerAfterQuest();

		Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest(quest.getSlotName(), "done");
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(quest.welcomeAfterQuestIsCompleted(), npc.get("text"));
	}

	@Test
	public final void doQuest() {
		MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		Player player = PlayerTestHelper.createPlayer("player");
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), npc.get("text"));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), npc.get("text"));

		en.step(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("answer to quests accepted", quest.respondToQuestAcception(), npc.get("text"));
		assertTrue(player.hasQuest(quest.getSlotName()));
		assertFalse(npc.isTalking());
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());

		en.step(player, quest.getTriggerPhraseToEnumerateMissingItems().get(0));
		assertEquals("i have not brought anything yet it should be all needed items",
				"#one, #two, and #three", npc.get("text"));

		StackableItem item = new StackableItem("one", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		en.step(player, "yes");
		assertEquals("item brought", quest.askForItemsAfterPlayerSaidHeHasItems(), npc.get("text"));

		en.step(player, "one");
		assertEquals("item brought", quest.respondToItemBrought(), npc.get("text"));
		en.step(player, "one");
		assertEquals("item brought", quest.respondToOfferOfNotMissingItem(), npc.get("text"));
		npc.remove("text");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		en.step(player, quest.getTriggerPhraseToEnumerateMissingItems().get(0));
		List<String> missing = new LinkedList<String>(quest.getNeededItems());
		missing.remove("one");
		assertEquals("two and three are missing", "#two and #three", npc.get("text"));
		en.step(player, "two");
		assertEquals("item brought", quest.respondToOfferOfNotExistingItem("two"), npc.get("text"));

		item = new StackableItem("two", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		item = new StackableItem("three", "", "", null);
		item.setQuantity(10);
		item.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(item);
		en.step(player, "three");
		assertEquals("item brought", quest.respondToItemBrought(), npc.get("text"));
		en.step(player, "two");
		assertEquals("last item brought", quest.respondToLastItemBrought(), npc.get("text"));
	}

	@Test
	public final void testAcceptQuest() {
		MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		SpeakerNPC npc = new SpeakerNPC("npcAccept");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		Player player = PlayerTestHelper.createPlayer("player");

		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), npc.get("text"));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), npc.get("text"));

		en.step(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("answer to quests accepted", quest.respondToQuestAcception(), npc.get("text"));
	}

	@Test
	public final void testRejectQuest() {
		MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
		};
		SpeakerNPC npc = new SpeakerNPC("npcReject");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.addToWorld();

		Player player = PlayerTestHelper.createPlayer("player");
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("first hi", quest.welcomeBeforeStartingQuest(), npc.get("text"));
		npc.put("text", "");

		en.step(player, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertEquals("answer to quest", quest.respondToQuest(), npc.get("text"));

		en.step(player, "no");
		assertEquals("answer to quests accepted", quest.respondToQuestRefusal(), npc.get("text"));
	}

	class MockBringListOfItemsQuest implements BringListOfItemsQuest {
		private SpeakerNPC npc;

		private boolean isWelcomingAfterQuests;

		// makes our live easier
		public void setNpc(SpeakerNPC npc) {
			this.npc = npc;
		}

		public String askForItemsAfterPlayerSaidHeHasItems() {
			return "askForItemsAfterPlayerSaidHeHasItems";
		}

		public String askForMissingItems(List<String> missingItems) {
			return Grammar.enumerateCollection(missingItems);
		}

		public List<String> getAdditionalTriggerPhraseForQuest() {
			return Arrays.asList(new String[] { "getAdditionalTriggerPhraseForQuest" });
		}

		public SpeakerNPC getNPC() {
			if (npc == null) {

				PlayerTestHelper.generateNPCRPClasses();
				npc = new SpeakerNPC("MockBringListOfItemsQuest");
			}
			return npc;
		}

		public List<String> getNeededItems() {
			return Arrays.asList(new String[] { "one", "two", "three" });
		}

		public String getSlotName() {
			return "MockBringListOfItemsQuest";
		}

		public List<String> getTriggerPhraseToEnumerateMissingItems() {
			return Arrays.asList("getTriggerPhraseToEnumerateMissingItems");
		}

		public String respondToItemBrought() {
			return "respondToItemBrought";
		}

		public String respondToLastItemBrought() {
			return "respondToLastItemBrought";
		}

		public String respondToOfferOfNotExistingItem(String itemName) {
			return "respondToOfferOfNotExistingItem" + itemName;
		}

		public String respondToOfferOfNotMissingItem() {
			return "respondToOfferOfNotMissingItem";
		}

		public String respondToOfferOfNotNeededItem() {
			return "respondToOfferOfNotNeededItem";
		}

		public String respondToPlayerSayingHeHasNoItems(List<String> missingItems) {
			return "respondToPlayerSayingHeHasNoItems";
		}

		public String respondToQuest() {
			return "respondToQuest";
		}

		public String respondToQuestAcception() {
			return "respondToQuestAcception";
		}

		public String respondToQuestAfterItHasAlreadyBeenCompleted() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		public String respondToQuestRefusal() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		public void rewardPlayer(Player player) {
			
		}

		public boolean shouldWelcomeAfterQuestIsCompleted() {
			return isWelcomingAfterQuests;
		}

		public String welcomeAfterQuestIsCompleted() {
			return "shouldWelcomeAfterQuestIsCompleted";
		}

		public String welcomeBeforeStartingQuest() {
			return "welcomeBeforeStartingQuest";
		}

		public String welcomeDuringActiveQuest() {
			return "welcomeDuringActiveQuest";
		}

		void setWelcomingAfterQuests(boolean isWelcomingAfterQuests) {
			this.isWelcomingAfterQuests = isWelcomingAfterQuests;
		}

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

		public String askForItemsAfterPlayerSaidHeHasItems() {
			return null;
		}

		public String askForMissingItems(List<String> missingItems) {
			return null;
		}

		public List<String> getAdditionalTriggerPhraseForQuest() {
			return null;
		}

		public SpeakerNPC getNPC() {
			if (npc == null) {

				PlayerTestHelper.generateNPCRPClasses();
				npc = new SpeakerNPC("MockBringListOfItemsQuest");
			}
			return npc;
		}

		public List<String> getNeededItems() {
			return null;
		}

		public String getSlotName() {

			return "NullValueMockBringListOfItemsQuest";
		}

		public List<String> getTriggerPhraseToEnumerateMissingItems() {
			return null;
		}

		public String respondToItemBrought() {

			return null;
		}

		public String respondToLastItemBrought() {

			return null;
		}

		public String respondToOfferOfNotExistingItem(String itemName) {

			return null;
		}

		public String respondToOfferOfNotMissingItem() {

			return null;
		}

		public String respondToOfferOfNotNeededItem() {
			return null;
		}

		public String respondToPlayerSayingHeHasNoItems(List<String> missingItems) {
			return null;
		}

		public String respondToQuest() {
			return "respondToQuest";
		}

		public String respondToQuestAcception() {
			return "respondToQuestAcception";
		}

		public String respondToQuestAfterItHasAlreadyBeenCompleted() {
			return "respondToQuestAfterItHasAlreadyBeenCompleted";
		}

		public String respondToQuestRefusal() {
			return "respondToQuestRefusal";
		}

		public void rewardPlayer(Player player) {

		}

		public boolean shouldWelcomeAfterQuestIsCompleted() {
			return false;
		}

		public String welcomeAfterQuestIsCompleted() {
			return "welcomeAfterQuestIsCompleted";
		}

		public String welcomeBeforeStartingQuest() {
			return "welcomeBeforeStartingQuest";
		}

		public String welcomeDuringActiveQuest() {
			return "welcomeDuringActiveQuest";
		}

		public void setNpc(SpeakerNPC npc) {
			this.npc = npc;
		}

		public double getKarmaDiffForQuestResponse() {
			return 0;
		}
	}
}
