package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.library.LibrarianNPC;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class LookBookforCerylTest {
	@BeforeClass
	public static void setupClass(){
		Log4J.init();
	}

	SpeakerNPC jynath;
	@Before
	public  void setUp() throws Exception {
		jynath = SpeakerNPCTestHelper.createSpeakerNPC("Jynath");
		jynath.addGoodbye();
		NPCList.get().add(jynath);
		new LibrarianNPC().configureZone(new StendhalRPZone("testzone"), null);

	}

	@After
	public void tearDown() throws Exception {
		NPCList.get().remove("ceryl");
		NPCList.get().remove("Jynath");
	}






	@Test
	public void doQuest() throws Exception {
		LookBookforCeryl quest = new LookBookforCeryl();
		SpeakerNPC ceryl = NPCList.get().get("ceryl");
		quest.init("Ceryl needs a book");
		quest.addToWorld();
		Player pl = PlayerTestHelper.createPlayer();
		assertFalse(quest.isStarted(pl));
		assertFalse(quest.isCompleted(pl));
		Engine cerylEngine = ceryl.getEngine();
		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Greetings! How may I help you?",ceryl.get("text"));
		cerylEngine.step(pl, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertTrue(ceryl.isTalking());
		assertEquals("I am looking for a very special #book.",ceryl.get("text"));
		cerylEngine.step(pl, "book");
		assertTrue(ceryl.isTalking());
		assertEquals("Could you ask #Jynath to return her book? She's had it for months now, and people are looking for it.",ceryl.get("text"));
		cerylEngine.step(pl, ConversationPhrases.YES_MESSAGES.get(0));
		assertTrue(ceryl.isTalking());
		assertEquals("Great! Please get me it as quickly as possible... there's a huge waiting list!",ceryl.get("text"));
		assertEquals("start",pl.getQuest("ceryl_book"));
		cerylEngine.step(pl, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(ceryl.isTalking());
		Engine jynathEngine = jynath.getEngine();
		jynathEngine.step(pl,"Hi");
		assertTrue(jynath.isTalking());
		assertEquals("Oh, Ceryl's looking for that book back? My goodness! I completely forgot about it... here you go!",jynath.get("text"));
		assertTrue(pl.isEquipped("book_black"));
		jynathEngine.step(pl,"bye");
		assertFalse(jynath.isTalking());

		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Oh, you got the book back! Phew, thanks!",ceryl.get("text"));
		cerylEngine.step(pl, "bye");

		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Greetings! How may I help you?",ceryl.get("text"));
		cerylEngine.step(pl, "quest");
		assertTrue(ceryl.isTalking());
		assertEquals("I have nothing for you now.",ceryl.get("text"));

	}


	@Test
	public final void testAddToWorld() {

		LookBookforCeryl quest;
		quest = new LookBookforCeryl();
		quest.addToWorld();
	}

	@Test
	@Ignore
	public final void testGetHistory() {
		fail("Not yet implemented");
	}



	@Test
	@Ignore
	public final void testGetHint() {
		fail("Not yet implemented");
	}

	@Test
	public final void testIsCompleted() {
		LookBookforCeryl quest;
		quest = new LookBookforCeryl();
		quest.init("Ceryl needs a book");
		quest.addToWorld();
		Player pl = PlayerTestHelper.createPlayer();
		assertFalse(quest.isCompleted(pl));
		pl.setQuest("ceryl_book", "done");
		assertTrue(pl.hasQuest("ceryl_book"));
		assertTrue(pl.isQuestCompleted("ceryl_book"));
		assertTrue(quest.isCompleted(pl));

	}

	@Test
	public final void testIsRepeatable() {
		LookBookforCeryl quest = new LookBookforCeryl();
		assertFalse(quest.isRepeatable(null));
	}

	@Test
	public final void testIsStarted() {
		LookBookforCeryl quest = new LookBookforCeryl();
		quest.init("blabla");
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(bob.hasQuest("ceryl_book"));
		assertFalse(quest.isStarted(bob));
		bob.setQuest("ceryl_book", "done");
		assertTrue(bob.hasQuest("ceryl_book"));
		assertTrue(quest.isStarted(bob));
	}

	@Test
	public final void testGetName() {
		LookBookforCeryl quest = new LookBookforCeryl();
		assertEquals(null,quest.getName());
		quest.init("testname");
		assertEquals("testname",quest.getName());
	}

}
