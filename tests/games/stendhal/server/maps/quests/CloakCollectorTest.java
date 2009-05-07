package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		assertEquals(cc.welcomeBeforeStartingQuest(), npc.get("text"));

		en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
		assertEquals(cc.respondToQuest(), npc.get("text"));

		en.stepTest(monica, cc.getTriggerPhraseToEnumerateMissingItems().get(0));
		assertEquals(cc.firstAskForMissingItems(cc.getNeededItems()), npc.get("text"));

		en.stepTest(monica, "no");
		assertEquals(cc.respondToQuestRefusal(), npc.get("text"));
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
		assertEquals(cc.welcomeBeforeStartingQuest(), npc.get("text"));

		en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
		assertEquals(cc.respondToQuest(), npc.get("text"));

		en.stepTest(monica, "elf cloak");
		assertEquals(
				"You haven't seen one before? Well, it's a elf cloak. So, will you find them all?",
				npc.get("text"));

		en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(cc.respondToQuestAcception(), npc.get("text"));
		assertFalse(npc.isTalking());
		npc.remove("text");

		assertTrue("the quest was accepted, so it should be started",
				cc.isStarted(monica));
		assertFalse(cc.isCompleted(monica));

		en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(cc.welcomeDuringActiveQuest(), npc.get("text"));
		npc.remove("text");
		en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(cc.askForItemsAfterPlayerSaidHeHasItems(), npc.get("text"));

		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToOfferOfNotExistingItem("elf cloak"),
				npc.get("text"));

		Item cloak = new Item("elf cloak", "", "", null);
		monica.getSlot("bag").add(cloak);
		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToItemBrought(), npc.get("text"));
		en.stepTest(monica, "elf cloak");
		assertEquals(cc.respondToOfferOfNotMissingItem(), npc.get("text"));

		cloak = new Item("stone cloak", "", "", null);
		monica.getSlot("bag").add(cloak);

		for (final String cloakName : cc.getNeededItems()) {
			cloak = new Item(cloakName, "", "", null);
			monica.getSlot("bag").add(cloak);
			en.step(monica, cloakName);
		}

		assertEquals(cc.respondToLastItemBrought(), npc.get("text"));
		en.step(monica, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertTrue(cc.isCompleted(monica));
	}

	@Test
	public final void testGetSlotName() {
		final CloakCollector cc = new CloakCollector();
		assertEquals("cloaks_collector", cc.getSlotName());
	}

	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		final CloakCollector cc = new CloakCollector();
		assertFalse(cc.shouldWelcomeAfterQuestIsCompleted());
	}

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
