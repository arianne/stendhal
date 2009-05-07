package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.fishermans_hut.FishermanNPC;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test for the "Lookup Quote" quest.
 * 
 * @author Martin Fuchs
 */
public class LookUpQuoteTest extends ZonePlayerAndNPCTestImpl {

	private static final String QUEST_SLOT = "get_fishing_rod";
	private static final String ZONE_NAME = "testzone";

	private static final char TOMMY_FIRST_LETTER = 'T';
	private static final char JACKY_FIRST_LETTER = 'J';
	private static final char BULLY_FIRST_LETTER = 'B';
	private static final char SODY_FIRST_LETTER = 'S';
	private static final char HUMPREY_FIRST_LETTER = 'H';
	private static final char MONTY_FIRST_LETTER = 'M';
	private static final char CHARBY_FIRST_LETTER = 'C';
	private static final char ALLY_FIRST_LETTER = 'A';

	private static LookUpQuote quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new FishermanNPC());

		quest = new LookUpQuote();
		quest.addToWorld();
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public LookUpQuoteTest() {
		super(ZONE_NAME, "Pequod");
	}

	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Pequod");
		assertNotNull(npc);
		final Engine en1 = npc.getEngine();
		assertTrue("test text recognition with additional text after 'hi'",
				en1.step(player, "hi Pequod"));
		assertTrue(npc.isTalking());
		assertEquals(
				"Hello newcomer! I can #help you on your way to become a real fisherman!",
				npc.get("text"));
		assertTrue("test text recognition with additional text after 'bye'",
				en1.step(player, "bye bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));
	}

	@Test
	public void testDoQuest() {
		final SpeakerNPC pequodNpc = getNPC("Pequod");
		assertNotNull(pequodNpc);
		final Engine pequodEngine = pequodNpc.getEngine();
		assertTrue("test saying 'Hello' instead of 'hi'", pequodEngine.step(
				player, "Hello"));
		assertEquals(
				"Hello newcomer! I can #help you on your way to become a real fisherman!",
				pequodNpc.get("text"));

		assertTrue(pequodEngine.step(player, "help"));
		assertEquals(
				"Nowadays you can read signposts, books and other things here in Faiumoni.",
				pequodNpc.get("text"));

		assertTrue(pequodEngine.step(player, "quest"));
		assertEquals(
				"Well, I once had a book with quotes of famous fishermen, but I lost it. And now I cannot remember a certain quote. Can you look it up for me?",
				pequodNpc.get("text"));

		assertTrue(pequodEngine.step(player, "yes"));
		final String reply = pequodNpc.get("text");
		assertTrue(reply.startsWith("Please look up the famous quote by fisherman "));
		// fish out the fisherman's man from Pequod's reply
		final String fisherman = reply.substring(45, reply.length() - 1);
		assertTrue(player.hasQuest(QUEST_SLOT));
		assertTrue(player.getQuest(QUEST_SLOT).startsWith("fisherman "));

		assertTrue(pequodEngine.step(player, "task"));
		assertTrue(pequodNpc.get("text").startsWith("I already asked you for a favor already! Have you already looked up the famous quote by fisherman "));

		assertTrue(pequodEngine.step(player, "bye"));
		assertEquals("Goodbye.", pequodNpc.get("text"));

		// bother Pequod again
		assertTrue(pequodEngine.step(player, "hi"));
		assertTrue(pequodNpc.get("text").startsWith("Welcome back! Did you look up the famous quote by fisherman "));
		assertTrue("lie", pequodEngine.step(player, "yes")); 
		assertEquals("So, what is it?", pequodNpc.get("text"));
		assertTrue(pequodEngine.step(player, "bye"));
		assertEquals("Good bye - see you next time!", pequodNpc.get("text"));

		// determine the correct answer
		String quote = "";

		switch(fisherman.charAt(0)) {
        	case BULLY_FIRST_LETTER:
        		quote = "Clownfish are always good for a laugh.";
        		break;
        	case JACKY_FIRST_LETTER:	
        		quote = "Don't mistake your trout for your old trout, she wouldn't taste so good.";
        		break;
        	case TOMMY_FIRST_LETTER:
        		quote = "I wouldn't trust a surgeonfish in a hospital, there's something fishy about them.";
        		break;
        	case SODY_FIRST_LETTER:	
        		quote = "Devout Crustaceans believe in the One True Cod.";
        		break;
        	case HUMPREY_FIRST_LETTER:
        		quote = "I don't understand why no-one buys my fish. The sign says 'Biggest Roaches in town'.";
        		break;
        	case MONTY_FIRST_LETTER:
        		quote = "My parrot doesn't like to sit on a perch. He says it smells fishy.";
        		break;
        	case CHARBY_FIRST_LETTER:
        		quote = "That fish restaurant really overcooks everything. It even advertises char fish.";
        		break;
        	case ALLY_FIRST_LETTER:	
        		quote = "Holy mackerel! These chips are tasty.";
        		break;
        	default:
        		fail("unknown fisherman" + fisherman);
        }

		// bother Pequod again
		assertTrue(pequodEngine.step(player, "hi"));
		assertTrue(pequodNpc.get("text").startsWith("Welcome back! Did you look up the famous quote by fisherman "));
		assertTrue("lie", pequodEngine.step(player, "yes")); 
		assertEquals("So, what is it?", pequodNpc.get("text"));

		assertTrue(pequodEngine.step(player, quote));
		assertEquals("Oh right, that's it! How could I forget this? Here, take this handy fishing rod as an acknowledgement of my gratitude!",
				pequodNpc.get("text"));
		assertEquals("done", player.getQuest(QUEST_SLOT));
		assertTrue(player.isQuestCompleted(QUEST_SLOT));

		assertTrue(pequodEngine.step(player, "bye"));
		assertEquals("Goodbye.", pequodNpc.get("text"));

		// bother Pequod again
		assertTrue(pequodEngine.step(player, "hi"));
		assertTrue(pequodNpc.get("text").startsWith("Welcome back!"));
		assertTrue(pequodEngine.step(player, "quest"));
		assertEquals("No, thanks. I have all I need.", pequodNpc.get("text"));
	}

	@Test
	public final void testGetHistory() {
		assertTrue(quest.getHistory(player).isEmpty());

		player.setQuest(QUEST_SLOT, "fisherman Bully");
		assertEquals(1, quest.getHistory(player).size());
		assertEquals(Arrays.asList("FIRST_CHAT"),
				quest.getHistory(player));

		player.setQuest(QUEST_SLOT, "done");
		assertEquals(2, quest.getHistory(player).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "DONE"),
				quest.getHistory(player));
	}

}
