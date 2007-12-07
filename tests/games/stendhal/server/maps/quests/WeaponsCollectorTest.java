package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Grammar;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class WeaponsCollectorTest {
	@BeforeClass
	public static void setupclass() {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		NPCList.get().remove("Balduin");
	}

	@Test
	public final void testGetSlotName() {
		WeaponsCollector wc = new WeaponsCollector();
		assertEquals("weapons_collector", wc.getSlotName());
	}

	@Test
	public final void rejectQuest() {
		NPCList.get().add(new SpeakerNPC("Balduin"));
		WeaponsCollector wc = new WeaponsCollector();
		wc.addToWorld();
		SpeakerNPC npc = wc.getNPC();
		Engine en = npc.getEngine();
		Player pl = PlayerTestHelper.createPlayer();

		assertTrue(en.stepTest(pl, "hi"));
		assertEquals(wc.welcomeBeforeStartingQuest(), npc.get("text"));

		assertTrue(en.stepTest(pl, "quest"));
		assertEquals(wc.respondToQuest(), npc.get("text"));

		assertTrue(en.stepTest(pl, "no"));
		assertEquals(wc.respondToQuestRefusal(), npc.get("text"));
	}

	@Test
	@Ignore
	public final void doQuest() {
		NPCList.get().add(new SpeakerNPC("Balduin"));
		WeaponsCollector wc = new WeaponsCollector();
		wc.init("weaponscollector_quest");
		wc.addToWorld();
		SpeakerNPC npc = wc.getNPC();
		Engine en = npc.getEngine();
		Player pl = PlayerTestHelper.createPlayer("pl");

		assertTrue(en.stepTest(pl, "hello"));
		assertEquals(wc.welcomeBeforeStartingQuest(), npc.get("text"));

		assertTrue(en.stepTest(pl, "quest"));
		assertEquals(wc.respondToQuest(), npc.get("text"));

		assertTrue(en.stepTest(pl, "collection"));
		assertEquals(wc.askForMissingItems(wc.getNeededItems()), npc.get("text"));

		assertTrue(en.stepTest(pl, "no"));
System.out.println(npc.get("text"));
		assertEquals("Let me know as soon as you find .... Farewell.", npc.get("text"));

		assertTrue(en.stepTest(pl, "yes"));
System.out.println(npc.get("text"));
		assertEquals("What is it that you found?", npc.get("text"));

		assertTrue(en.stepTest(pl, "bardiche"));
System.out.println(npc.get("text"));
		assertEquals("You haven't seen one before? Well, it's a white_cloak. So, will you find them all?", npc.get("text"));

/*TODO
		en.stepTest(pl, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(wc.respondToQuestAcception(), npc.get("text"));
		assertFalse(npc.isTalking());
		npc.remove("text");

		assertTrue("the quest was accepted, so it should be started", wc.isStarted(pl));
		assertFalse(wc.isCompleted(pl));

		assertTrue(en.stepTest(pl, ConversationPhrases.GREETING_MESSAGES.get(0)));
		assertEquals(wc.welcomeDuringActiveQuest(), npc.get("text"));
		npc.remove("text");
		en.stepTest(pl, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals(wc.askForItemsAfterPlayerSaidHeHasItems(), npc.get("text"));

		en.stepTest(pl, "bardiche");
		assertEquals(wc.respondToOfferOfNotExistingItem("bardiche"), npc.get("text"));

		Item cloak = new Item("bardiche", "", "", null);
		pl.getSlot("bag").add(cloak);

		assertTrue(en.stepTest(pl, "bardiche"));
		assertEquals(wc.respondToItemBrought(), npc.get("text"));

		assertTrue(en.stepTest(pl, "bardiche"));
		assertEquals(wc.respondToOfferOfNotMissingItem(), npc.get("text"));

		cloak = new Item("stone_cloak", "", "", null);
		pl.getSlot("bag").add(cloak);

		for(String cloakName : wc.getNeededItems()) {
			cloak = new Item(cloakName, "", "", null);
			pl.getSlot("bag").add(cloak);
			assertTrue(en.step(pl, cloakName));
		}

		assertEquals(wc.respondToLastItemBrought(), npc.get("text"));
		assertTrue(en.step(pl, ConversationPhrases.GOODBYE_MESSAGES.get(0)));
		assertTrue(wc.isCompleted(pl));
*/
	}

	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		WeaponsCollector wc = new WeaponsCollector();
		assertFalse(wc.shouldWelcomeAfterQuestIsCompleted());
	}

	@Test
	@Ignore
	public final void testRewardPlayer() {
		WeaponsCollector wc = new WeaponsCollector();
		Player player = PlayerTestHelper.createPlayer();
		double oldKarma = player.getKarma();
		wc.rewardPlayer(player);
		assertTrue(player.isEquipped("ice_sword"));
		assertEquals(oldKarma + 5.0, player.getKarma(), 0.01);
		assertEquals(2500, player.getXP());
	}
}
