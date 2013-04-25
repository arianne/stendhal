package games.stendhal.server.maps.ados.city;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.bakery.ChefNPC;
import games.stendhal.server.maps.semos.bakery.ShopAssistantNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class HolidayingWomanNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";
	private static final String TASTED_EVERYTHING_REPLY = "I think I've tasted everything";
	private static final String BREAD_DESCRIPTION = "Erna bakes loaves of bread, which need 2 sacks of flour each.";
	private static final String SANDWICH_DESCRIPTION = "Leander makes sandwiches, which need 1 loaf of bread, 1 piece of ham, and 2 pieces of cheese each.";

	private Player player;
	private SpeakerNPC aliceNpc;
	private Engine aliceEngine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new HolidayingWomanNPC(), new ChefNPC(), new ShopAssistantNPC());
	}
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		player = createPlayer("player");
		aliceNpc = SingletonRepository.getNPCList().get("Alice Farmer");
		aliceEngine = aliceNpc.getEngine();
	}

	public HolidayingWomanNPCTest() {
		super(ZONE_NAME, "Alice Farmer", "Leander", "Erna");
	}

	@Test
	public void testDialogue() {
		startConversation();

		askForFoodList();
		checkReply("bread", BREAD_DESCRIPTION);
		checkReply("sandwich", SANDWICH_DESCRIPTION);
		checkReply("sandwich.", SANDWICH_DESCRIPTION);

		endConversation();
	}
	
	private void startConversation() {
		aliceEngine.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(aliceNpc.isTalking());
		assertEquals("Hello.", getReply(aliceNpc));
	}
	
	private void askForFoodList() {
		aliceEngine.step(player, "food");
		assertTrue(aliceNpc.isTalking());
		String listOfFoodReply = getReply(aliceNpc);
		assertTrue(listOfFoodReply.startsWith(TASTED_EVERYTHING_REPLY));
		assertTrue(listOfFoodReply.contains("#bread"));
		assertTrue(listOfFoodReply.contains("#sandwich"));
	}
	
	private void endConversation() {
		aliceEngine.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(aliceNpc.isTalking());
		assertEquals("Bye bye.", getReply(aliceNpc));
	}

	private void checkReply(String question, String expectedReply) {
		aliceEngine.step(player, question);
		assertTrue(aliceNpc.isTalking());
		assertEquals(expectedReply, getReply(aliceNpc));
	}
}
