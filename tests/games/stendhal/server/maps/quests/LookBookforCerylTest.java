package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.orril.magician_house.WitchNPC;
import games.stendhal.server.maps.semos.library.LibrarianNPC;

import java.util.Arrays;

import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class LookBookforCerylTest {
	private static final String CERYL_BOOK = "ceryl_book";

	@BeforeClass
	public static void setupClass() {
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
		Log4J.init();
	}

	private SpeakerNPC jynath;

	private SpeakerNPC ceryl;

	@Before
	public void setUp() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		new WitchNPC().configureZone(new StendhalRPZone("testzone"), null);
		new LibrarianNPC().configureZone(new StendhalRPZone("testzone"), null);
		jynath = SingletonRepository.getNPCList().get("jynath");
		ceryl = SingletonRepository.getNPCList().get("ceryl");
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("ceryl");
		SingletonRepository.getNPCList().remove("Jynath");
	}

	@Test
	public final void askJynathWithoutQuest() {
		final LookBookforCeryl quest = new LookBookforCeryl();
	
		quest.addToWorld();
		final Player pl = PlayerTestHelper.createPlayer("joe");
		assertFalse(quest.isStarted(pl));
		assertFalse(quest.isCompleted(pl));

		final Engine jynathEngine = jynath.getEngine();
		jynathEngine.step(pl, "Hi");
		assertTrue(jynath.isTalking());
		assertEquals("Greetings! How may I help you?", jynath.get("text"));

		jynathEngine.step(pl, "book");
		assertTrue(jynath.isTalking());
		assertEquals(
				"Sssh! I'm concentrating on this potion recipe... it's a tricky one.",
				jynath.get("text"));

		jynathEngine.step(pl, "bye");
		assertFalse(jynath.isTalking());
	}

	@Test
	public final void comeBackFromJynathWithoutBook() {
		final LookBookforCeryl quest = new LookBookforCeryl();
	
		quest.addToWorld();
		final Player pl = PlayerTestHelper.createPlayer("joe");
		pl.setQuest(CERYL_BOOK, "jynath");

		final Engine cerylEngine = ceryl.getEngine();
		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals(
				"Haven't you got that #book back from #Jynath? Please go look for it, quickly!",
				ceryl.get("text"));

		assertFalse(pl.hasQuest(CERYL_BOOK));
	}

	@Test
	public void doQuest() throws Exception {
		final LookBookforCeryl quest = new LookBookforCeryl();
	
		quest.addToWorld();
		final Player pl = PlayerTestHelper.createPlayer("player");
		assertFalse(quest.isStarted(pl));
		assertFalse(quest.isCompleted(pl));
		final Engine cerylEngine = ceryl.getEngine();
		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Greetings! How may I help you?", ceryl.get("text"));
		cerylEngine.step(pl, ConversationPhrases.QUEST_MESSAGES.get(0));
		assertTrue(ceryl.isTalking());
		assertEquals("I am looking for a very special #book.",
				ceryl.get("text"));
		cerylEngine.step(pl, "book");
		assertTrue(ceryl.isTalking());
		assertEquals(
				"Could you ask #Jynath to return her book? She's had it for months now, and people are looking for it.",
				ceryl.get("text"));
		cerylEngine.step(pl, ConversationPhrases.YES_MESSAGES.get(0));
		assertTrue(ceryl.isTalking());
		assertEquals(
				"Great! Please get me it as quickly as possible... there's a huge waiting list!",
				ceryl.get("text"));
		assertEquals("start", pl.getQuest(LookBookforCerylTest.CERYL_BOOK));
		cerylEngine.step(pl, "book");
		assertTrue(ceryl.isTalking());
		assertEquals("I really need that book now! Go to talk with #Jynath.",
				ceryl.get("text"));
		cerylEngine.step(pl, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(ceryl.isTalking());
		final Engine jynathEngine = jynath.getEngine();
		jynathEngine.step(pl, "Hi");
		assertTrue(jynath.isTalking());
		assertEquals(
				"Oh, Ceryl's looking for that book back? My goodness! I completely forgot about it... here you go!",
				jynath.get("text"));
		assertTrue(pl.isEquipped("black book"));
		jynathEngine.step(pl, "bye");
		assertFalse(jynath.isTalking());

		jynathEngine.step(pl, "Hi");
		assertTrue(jynath.isTalking());
		assertEquals(
				"You'd better take that book back to #Ceryl quickly... he'll be waiting for you.",
				jynath.get("text"));

		jynathEngine.step(pl, "book");
		assertTrue(jynath.isTalking());
		assertEquals(
				"You'd better take that book back to #Ceryl quickly... he'll be waiting for you.",
				jynath.get("text"));

		jynathEngine.step(pl, "bye");
		assertFalse(jynath.isTalking());

		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Oh, you got the book back! Phew, thanks!",
				ceryl.get("text"));
		cerylEngine.step(pl, "bye");

		cerylEngine.step(pl, "Hi");
		assertTrue(ceryl.isTalking());
		assertEquals("Greetings! How may I help you?", ceryl.get("text"));
		cerylEngine.step(pl, "quest");
		assertTrue(ceryl.isTalking());
		assertEquals("I have nothing for you now.", ceryl.get("text"));

	}

	@Test
	public final void testAddToWorld() {
		LookBookforCeryl quest;
		quest = new LookBookforCeryl();
		quest.addToWorld();
	}

	@Test
	public final void testGetHistory() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		LookBookforCeryl quest;
		quest = new LookBookforCeryl();
		quest.addToWorld();
	
		assertTrue(quest.getHistory(pl).isEmpty());

		pl.setQuest(CERYL_BOOK, "rejected");
		assertEquals(2, quest.getHistory(pl).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "QUEST_REJECTED"),
				quest.getHistory(pl));

		pl.setQuest(CERYL_BOOK, "start");
		assertEquals(2, quest.getHistory(pl).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "QUEST_ACCEPTED"),
				quest.getHistory(pl));

		pl.setQuest(CERYL_BOOK, "jynath");
		assertEquals(3, quest.getHistory(pl).size());
		assertEquals(
				Arrays.asList("FIRST_CHAT", "QUEST_ACCEPTED", "LOST_ITEM"),
				quest.getHistory(pl));

		final Item item = SingletonRepository.getEntityManager().getItem(
				"black book");
		assertNotNull(item);
		item.setBoundTo(pl.getName());
		pl.equipOrPutOnGround(item);
		assertEquals(3, quest.getHistory(pl).size());
		assertEquals(
				Arrays.asList("FIRST_CHAT", "QUEST_ACCEPTED", "FOUND_ITEM"),
				quest.getHistory(pl));

		pl.setQuest(CERYL_BOOK, "done");
		assertEquals(4, quest.getHistory(pl).size());
		assertEquals(Arrays.asList("FIRST_CHAT", "QUEST_ACCEPTED",
				"FOUND_ITEM", "DONE"), quest.getHistory(pl));
	}

	@Test
	public final void testIsCompleted() {
		LookBookforCeryl quest;
		quest = new LookBookforCeryl();

		quest.addToWorld();
		final Player pl = PlayerTestHelper.createPlayer("player");
		assertFalse(quest.isCompleted(pl));
		pl.setQuest(LookBookforCerylTest.CERYL_BOOK, "done");
		assertTrue(pl.hasQuest(LookBookforCerylTest.CERYL_BOOK));
		assertTrue(pl.isQuestCompleted(LookBookforCerylTest.CERYL_BOOK));
		assertTrue(quest.isCompleted(pl));
	}

	@Test
	public final void testIsRepeatable() {
		final LookBookforCeryl quest = new LookBookforCeryl();
		assertFalse(quest.isRepeatable(null));
	}

	@Test
	public final void testIsStarted() {
		final LookBookforCeryl quest = new LookBookforCeryl();
	
		final Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(bob.hasQuest(LookBookforCerylTest.CERYL_BOOK));
		assertFalse(quest.isStarted(bob));
		bob.setQuest(LookBookforCerylTest.CERYL_BOOK, "done");
		assertTrue(bob.hasQuest(LookBookforCerylTest.CERYL_BOOK));
		assertTrue(quest.isStarted(bob));
	}



}
