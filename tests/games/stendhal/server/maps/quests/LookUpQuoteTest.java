package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.fishermans_hut.FishermanNPC;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test for the "Lookup Quote" quest.
 * 
 * @author Martin Fuchs
 */
public class LookUpQuoteTest {

	static final String QUEST_SLOT = "get_fishing_rod";

	private static LookUpQuote quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		FishermanNPC pequodconf = new FishermanNPC();
		pequodconf.configureZone(new StendhalRPZone("testzone"), null);

		quest = new LookUpQuote();
		quest.addToWorld();
		quest.init("Lookup Quote");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		PlayerTestHelper.resetNPC("Pequod");
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Pequod");
		assertNotNull(npc);
		Engine en1 = npc.getEngine();
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
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC pequodNpc = NPCList.get().get("Pequod");
		assertNotNull(pequodNpc);
		Engine pequodEngine = pequodNpc.getEngine();
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
		String reply = pequodNpc.get("text");
		assertTrue(reply.startsWith("Please look up the famous quote by fisherman "));
		// fish out the fisherman's man from Pequod's reply
		String fisherman = reply.substring(45, reply.length() - 1);
		assertTrue(player.hasQuest(QUEST_SLOT));
		assertTrue(player.getQuest(QUEST_SLOT).startsWith("fisherman "));

		assertTrue(pequodEngine.step(player, "task"));
		assertTrue(pequodNpc.get("text").startsWith("I already asked you for a favor already! Have you already looked up the famous quote by fisherman "));

		assertTrue(pequodEngine.step(player, "bye"));
		assertEquals("Goodbye.", pequodNpc.get("text"));

		// bother Pequod again
		assertTrue(pequodEngine.step(player, "hi"));
		assertTrue(pequodNpc.get("text").startsWith("Welcome back! Did you look up the famous quote by fisherman "));
		assertTrue(pequodEngine.step(player, "yes")); // lie
		assertEquals("So, what is it?", pequodNpc.get("text"));
		assertTrue(pequodEngine.step(player, "bye"));
		assertEquals("I think you made a mistake. Come back if you can tell me the correct quote.", pequodNpc.get("text"));

		// TODO mf - read the requested quote from the library instead of using hardcoded strings

		String quote = "";
        switch(fisherman.charAt(0)) {
        	case 'B':	// Bully
        		quote = "Clownfish are always good for a laugh.";
        		break;
        	case 'J':	// Jacky
        		quote = "Don't mistake your trout for your old trout, she wouldn't taste so good.";
        		break;
        	case 'T':	// Tommy
        		quote = "I wouldn't trust a surgeonfish in a hospital, there's something fishy about them.";
        		break;
        	case 'S':	// Sody
        		quote = "Devout Crustaceans believe in the One True Cod.";
        		break;
        	case 'H':	// Humphrey
        		quote = "I don't understand why noone buys my fish. The sign says 'Biggest Roaches in town'.";
        		break;
        	case 'M':	// Monty
        		quote = "My parrot doesn't like to sit on a perch. He says it smells fishy.";
        		break;
        	case 'C':	// Charby
        		quote = "That fish restaurant really overcooks everything. It even advertises char fish.";
        		break;
        	case 'A':	// Ally
        		quote = "Holy mackerel! These chips are tasty.";
        		break;
        	default:
        		fail("unknown fisherman" + fisherman);
        }

		// bother Pequod again
		assertTrue(pequodEngine.step(player, "hi"));
		assertTrue(pequodNpc.get("text").startsWith("Welcome back! Did you look up the famous quote by fisherman "));
		assertTrue("lie" , pequodEngine.step(player, "yes")); 
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
		Player pl = PlayerTestHelper.createPlayer("player");
		assertTrue(quest.getHistory(pl).isEmpty());

		pl.setQuest(QUEST_SLOT, "fisherman Bully");
		assertEquals(2, quest.getHistory(pl).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "GET_FISHING_ROD"),
				quest.getHistory(pl));

		pl.setQuest(QUEST_SLOT, "done");
		assertEquals(3, quest.getHistory(pl).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "GET_FISHING_ROD", "DONE"),
				quest.getHistory(pl));
	}
}
