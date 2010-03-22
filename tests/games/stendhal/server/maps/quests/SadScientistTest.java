package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.kalavan.castle.SadScientistNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.PlayerTestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;
import utilities.SpeakerNPCTestHelper;

public class SadScientistTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private SpeakerNPC mayor = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {


		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new SadScientistNPC().configureZone(zone, null);
		
		mayor = SpeakerNPCTestHelper.createSpeakerNPC("Mayor Sakhs");
		SingletonRepository.getNPCList().add(mayor);
		
		AbstractQuest quest = new SadScientist();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Vasi Elos");
		en = npc.getEngine();

		// -----------------------------------------------


		// -----------------------------------------------

		// [23:00] Admin kymara changed your state of the quest 'sad_scientist' from 'done' to 'null'
		// [23:00] Changed the state of quest 'sad_scientist' from 'done' to 'null'
		// [23:00] Script "AlterQuest.class" was successfully executed.
		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "no");
		assertEquals("If you change your mind please ask me again...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?", getReply(npc));
		en.step(player, "no");
		assertEquals("Go away before I kill you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?", getReply(npc));
		en.step(player, "gems");
		assertEquals("I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife?", getReply(npc));
		en.step(player, "legs");
		assertEquals("Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife? Can you bring what I need?", getReply(npc));
		en.step(player, "yes");
		assertEquals("I am waiting, Semos man.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello. Please return when you have everything I need for the jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to.", getReply(npc));

	}
	
	@Ignore
	public void testNextPartQuest() {
		// summon all the items needed:
		
		en.step(player, "hi");
		assertEquals("Hello. Did you bring what I need?", getReply(npc));
		en.step(player, "no");
		assertEquals("What a wasteful child.", getReply(npc));

		// -----------------------------------------------
		en.step(player, "hi");
		assertEquals("Hello. Did you bring what I need?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Wonderful! I will start my work. I can do this in very little time with the help of technology! Please come back in 20 minutes.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Do you think I can work that fast? Go away. Come back in just under 20 minutes.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Do you think I can work that fast? Go away. Come back in just under 20 minutes.", getReply(npc));

		// -----------------------------------------------

		// [23:03] Admin kymara changed your state of the quest 'sad_scientist' from 'making;1269298965037' to 'making;1'
		// [23:03] Changed the state of quest 'sad_scientist' from 'making;1269298965037' to 'making;1'

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("I finished the legs. But I cannot trust you. Before I give the jewelled legs to you, I need a message from my darling. Ask Mayor Sakhs for Vera. Can you do that for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh, thank you. I am waiting.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Please ask Mayor Sakhs about my wife Vera.", getReply(npc));

		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Mayor Sakhs");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("Welcome citizen! Do you need help?", getReply(npc));
		en.step(player, "vera");
		assertEquals("What? How do you know her? Well it is a sad story. She was picking arandula for Ilisa (they were friends) and she saw the catacombs entrance. 3 months later a young hero saw her, and she was a vampirette. What a sad story. I kept this for her husband. A letter. I think he is in Kalavan.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Have a good day and enjoy your stay!", getReply(npc));

		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Vasi Elos");
		en = npc.getEngine();
		
		// [23:03] You put a valuable item on the ground. Please note that it will expire in 10 minutes, as all items do. But in this case there is no way to restore it.
		en.step(player, "hi");
		assertEquals("Please ask Mayor Sakhs about my wife Vera.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello! Do you have anything for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh no! I feel the pain. I do not need to create those beautiful jewelled legs now. I want to transform them. I want to make them a symbol of pain. You! Go kill my brother, the Imperial Scientist Sergej Elos. Give me his blood.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Do it!", getReply(npc));
		// i don't understand this. it doesn't always do it. but no error. weird.
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("I am only in pain. Kill my brother and bring me his blood. It's all I want now.", getReply(npc));

		// -----------------------------------------------

		// [23:04] Sergej Elos has been killed by kymara
		// [23:04] kymara earns 1750 experience points.

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Ha, ha, ha! I will cover those jewelled legs with this blood and they will transform into a symbol of pain.", getReply(npc));
		en.step(player, "symbol");
		assertEquals("I am going to create a pair of black legs. Come back in 5 minutes.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("I did not finish decorating the legs. Please check back in 5 minutes.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("I did not finish decorating the legs. Please check back in about 4 and a half minutes.", getReply(npc));

		// -----------------------------------------------

		// [23:05] Admin kymara changed your state of the quest 'sad_scientist' from 'decorating;1269299078702' to 'decorating;1'
		// [23:05] Changed the state of quest 'sad_scientist' from 'decorating;1269299078702' to 'decorating;1'

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Here are the black legs. Now I beg you to wear them. The symbol of my pain is done. Fare thee well.", getReply(npc));
		// [23:05] kymara earns 10000 experience points.

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
	}
}
