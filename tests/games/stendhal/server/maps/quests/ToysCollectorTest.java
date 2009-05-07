package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;

import java.util.Arrays;

import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class ToysCollectorTest {
	private ToysCollector quest;

	@BeforeClass
	public static void setupFixture() {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		PlayerTestHelper.generateNPCRPClasses();
	}

	@Before
	public void setUp() throws Exception {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Anna"));
		quest = new ToysCollector();
	
		quest.addToWorld();
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("Anna");
	}

	@Test
	public final void testGetNeededItems() {
		assertEquals(Arrays.asList(new String[] { "teddy", "dice", "dress" }),
				quest.getNeededItems());
	}

	@Test
	public final void testGetSlotName() {
		assertEquals("toys_collector", quest.getSlotName());
	}

	@Test
	public final void testGetTriggerPhraseToEnumerateMissingItems() {
		assertEquals(ConversationPhrases.EMPTY,
				quest.getTriggerPhraseToEnumerateMissingItems());
	}

	@Test
	public final void testGetAdditionalTriggerPhraseForQuest() {
		assertEquals(Arrays.asList(new String[] { "toys" }),
				quest.getAdditionalTriggerPhraseForQuest());
	}

	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		assertTrue(quest.shouldWelcomeAfterQuestIsCompleted());
	}

}
