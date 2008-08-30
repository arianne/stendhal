package games.stendhal.server.maps.quests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.nalwor.postoffice.PostNPC;
import games.stendhal.server.maps.nalwor.bank.BankNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class TakeGoldforGrafindleTest {


	private static String questSlot = "grafindle_gold";
	
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new PostNPC().configureZone(zone, null);
		new BankNPC().configureZone(zone, null);
				
		final AbstractQuest quest = new TakeGoldforGrafindle();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Grafindle");
		en = npc.getEngine();
		
		final double karma = player.getKarma();
		
		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", npc.get("text"));
		en.step(player, "help");
		assertEquals("That room has two chests owned by this bank and two owned by Semos bank.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I need someone who can be trusted with #gold.", npc.get("text"));
		en.step(player, "gold");
		assertEquals("One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.", npc.get("text"));
		en.step(player, "Lorithien");
		assertEquals("She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Well, at least you are honest and told me from the start.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", npc.get("text"));
		assertThat(player.getQuest(questSlot), is("rejected"));
		assertThat(player.getKarma(), lessThan(karma));
		
		final double karma2 = player.getKarma();
		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", npc.get("text"));
		en.step(player, "quest");
		assertEquals("I need someone who can be trusted with #gold.", npc.get("text"));
		en.step(player, "gold");
		assertEquals("One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.", npc.get("text"));
		// note we spelt this wrong but it was understood
		en.step(player, "Lorithiten");
		assertEquals("She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you. I hope to see you soon with the gold bars ... unless you are tempted to keep them.", npc.get("text"));
		assertThat(player.getQuest(questSlot), is("start"));
		assertThat(player.getKarma(), greaterThan(karma2));
		
		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I need someone who can be trusted with #gold.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", npc.get("text"));
		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Lorithien");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("I'm so glad you're here! I'll be much happier when this gold is safely in the bank.", npc.get("text"));
		assertTrue(player.isEquipped("gold bar"));
		assertThat(player.getQuest(questSlot), is("lorithien"));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", npc.get("text"));
		
		en.step(player, "hi");
		assertEquals("Oh, please take that gold back to #Grafindle before it gets lost!", npc.get("text"));
		en.step(player, "grafindle");
		assertEquals("Grafindle is the senior banker here in Nalwor, of course!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", npc.get("text"));

		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Grafindle");
		en = npc.getEngine();
		final int xp = player.getXP();
		final double karma3 = player.getKarma();
		
		en.step(player, "hi");
		// [09:40] kymara earns 200 experience points.
		assertEquals("Oh, you brought the gold! Wonderful, I knew I could rely on you. Please, have this key to our customer room.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", npc.get("text"));
		assertThat(player.getXP(), greaterThan(xp));
		assertTrue(player.isQuestCompleted(questSlot));
		assertThat(player.getKarma(), greaterThan(karma3));
		assertFalse(player.isEquipped("gold bar"));
		assertTrue(player.isEquipped("nalwor bank key"));
		
		en.step(player, "hi");
		assertEquals("Greetings. If you need #help, please ask.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I ask only that you are honest.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, young human.", npc.get("text"));	
		
		npc = SingletonRepository.getNPCList().get("Lorithien");
		en = npc.getEngine();
		// return to post elf after quest done
		en.step(player, "hi");
		assertEquals("Hi, can I #help you?", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye - nice to meet you!", npc.get("text"));
	}
}