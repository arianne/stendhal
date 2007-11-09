package games.stendhal.server.maps.quests;

import static org.junit.Assert.*;

import java.util.Arrays;

import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerHelper;

public class ToysCollectorTest {
	ToysCollector quest;
	@BeforeClass
	static public  void setupFixture(){
		PlayerHelper.generateNPCRPClasses();

	}

	@Before
	public void setUp() throws Exception {
		NPCList.get().add(new SpeakerNPC("Anna"));
		quest = new ToysCollector();
		quest.addToWorld();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAddToWorld() {

		NPCList.get().add(new SpeakerNPC("Anna"));
		ToysCollector tc = new ToysCollector();
		tc.addToWorld();
	}

	@Test
	public final void testGetNeededItems() {

		assertEquals(Arrays.asList(new String[]{"teddy","dice","dress"}), quest.getNeededItems());
	}

	@Test
	public final void testGetSlotName() {
		assertEquals("toys_collector", quest.getSlotName());

	}

	@Test
	public final void testGetTriggerPhraseToEnumerateMissingItems() {
		assertEquals("list", quest.getTriggerPhraseToEnumerateMissingItems());
	}

	@Test
	public final void testGetAdditionalTriggerPhraseForQuest() {
		assertEquals(Arrays.asList(new String[]{"toys"}), quest.getAdditionalTriggerPhraseForQuest());

	}

	@Test
	public final void testWelcomeBeforeStartingQuest() {
		assertEquals("Mummy said, we are not allowed to talk to strangers. She is worried about that lost girl. But I'm bored. I want some #toys!",quest.welcomeBeforeStartingQuest());

	}

	@Test
	public final void testWelcomeDuringActiveQuest() {
		assertEquals("Hello! I'm still bored. Did you bring me toys?",quest.welcomeDuringActiveQuest());

	}

	@Test
	public final void testWelcomeAfterQuestIsCompleted() {
		assertEquals("Hi! I'm busy playing with my toys, no grown ups allowed.",quest.welcomeAfterQuestIsCompleted());

	}

	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		assertTrue(quest.shouldWelcomeAfterQuestIsCompleted());
	}

	@Test
	public final void testRespondToQuest() {
		assertEquals("I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?",quest.respondToQuest());

	}

	@Test
	public final void testRespondToQuestAfterItHasAlreadyBeenCompleted() {
		assertEquals("The toys are great! Thanks!",quest.respondToQuestAfterItHasAlreadyBeenCompleted());

	}

	@Test
	public final void testRespondToQuestAcception() {
		assertEquals("Hooray! How exciting. See you soon.",quest.respondToQuestAcception());
	}

	@Test
	public final void testRespondToQuestRefusal() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAskForMissingItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToPlayerSayingHeHasNoItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAskForItemsAfterPlayerSaidHeHasItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToItemBrought() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToLastItemBrought() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRewardPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotExistingItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotMissingItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotNeededItem() {
		fail("Not yet implemented");
	}

}
