package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.dressingroom_female.LifeguardNPC;
import games.stendhal.server.maps.athor.holiday_area.TouristFromAdosNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class SuntanCreamForZaraTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new TouristFromAdosNPC().configureZone(zone, null);
		new LifeguardNPC().configureZone(zone, null);

		AbstractQuest quest = new SuntanCreamForZara();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");

		questSlot = quest.getSlotName();
	}

	@Test
	public void testStartQuest() {

		npc = SingletonRepository.getNPCList().get("Zara");
		en = npc.getEngine();


		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Nice to meet you!", getReply(npc));
		en.step(player, "task");
		assertEquals("I fell asleep in the sun and now my skin is burnt. Can you bring me the magic #'suntan cream' that the #lifeguards produce?", getReply(npc));
		en.step(player, "suntan cream");
		assertEquals("The #lifeguards make a great cream to protect from the sun and to heal sunburns at the same time. Now, will you get it for me?", getReply(npc));
		en.step(player, "lifeguards");
		assertEquals("The lifeguards are called Pam and David. I think they are in the dressing rooms. So, will you ask them for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, but I would have had a nice reward for you...", getReply(npc));
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));
		en.step(player, "hi");
		assertEquals("Nice to meet you!", getReply(npc));
		en.step(player, "task");
		assertEquals("You refused to help me last time and my skin is getting worse. Please can you bring me the magic #'suntan cream' that the #lifeguards produce?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much. I'll be waiting here for your return!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));
		en.step(player, "hi");
		assertEquals("I know that the #'suntan cream' is hard to get, but I hope that you didn't forget my painful problem...", getReply(npc));
		en.step(player, "task");
		assertEquals("Did you forget that you promised me to ask the #lifeguards for #'suntan cream'?", getReply(npc));
		en.step(player, "suntan cream");
		assertEquals("The #lifeguards make a great cream to protect from the sun and to heal sunburns at the same time.", getReply(npc));
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));

		assertEquals(player.getQuest(questSlot), "start");
	}

	@Test
	public void testGetCream() {

		npc = SingletonRepository.getNPCList().get("Pam");
		en = npc.getEngine();

		// doesn't matter what the player 'suntan cream for zara' quest slot is for making cream - pam is just a producer

		en.step(player, "hi");
		assertEquals("Hallo!", getReply(npc));
		en.step(player, "suntan cream");
		assertEquals("David's and mine suntan cream is famous all over the island. But the way to the labyrinth entrance is blocked, so we can't get all the ingredients we need. If you bring me the things we need, I can #mix our special suntan cream for you.", getReply(npc));
		en.step(player, "mix");
		assertEquals("I can only mix a suntan cream if you bring me a #'bottle of minor potion', a #'sprig of arandula', and a #kokuda.", getReply(npc));
		en.step(player, "sprig of arandula");
		assertEquals("Arandula is a herb which is growing around Semos.", getReply(npc));
		en.step(player, "kokuda");
		assertEquals("We can't find the Kokuda herb which is growing on this island, because the entrance of the labyrinth, where you can find this herb, is blocked.", getReply(npc));
		en.step(player, "bottle of minor potion");
		assertEquals("It's a small bottle full of potion. You can buy it at several places.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Have fun!", getReply(npc));

		PlayerTestHelper.equipWithItem(player, "minor potion");
		PlayerTestHelper.equipWithItem(player, "kokuda");
		PlayerTestHelper.equipWithItem(player, "arandula");
		assertFalse(player.isEquipped("suntan cream"));

		en.step(player, "hi");
		assertEquals("Hallo!", getReply(npc));
		en.step(player, "mix");
		assertEquals("I need you to fetch me a #'bottle of minor potion', a #'sprig of arandula', and a #kokuda for this job, which will take 10 minutes. Do you have what I need?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, I will mix a suntan cream for you, but that will take some time. Please come back in 10 minutes.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Have fun!", getReply(npc));

		assertNotNull(player.getQuest("pamela_mix_cream"));
		assertFalse(player.isEquipped("minor potion"));
		assertFalse(player.isEquipped("kokuda"));
		assertFalse(player.isEquipped("arandula"));

		en.step(player, "hi");
		assertEquals("Welcome back! I'm still busy with your order to mix a suntan cream for you. Come back in 10 minutes to get it.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Have fun!", getReply(npc));

		// [10:02] Admin kymara changed your state of the quest 'pamela_mix_cream' from '1;suntan cream;1288519190459' to '1;suntan cream;0'
		// [10:02] Changed the state of quest 'pamela_mix_cream' from '1;suntan cream;1288519190459' to '1;suntan cream;0'
		player.setQuest("pamela_mix_cream", "1;suntan cream;0");

		final int xp = player.getXP();
		en.step(player, "hi");
		assertEquals("Welcome back! I'm done with your order. Here you have the suntan cream.", getReply(npc));
		// [10:02] kymara earns 1 experience point.
		en.step(player, "bye");
		assertEquals("Have fun!", getReply(npc));

		assertThat(player.getXP(), greaterThan(xp));
		// wow one whole xp

		assertTrue(player.isEquipped("suntan cream"));
	}

	@Test
	public void testCompleteQuest() {

		npc = SingletonRepository.getNPCList().get("Zara");
		en = npc.getEngine();

		player.setQuest(questSlot, "start");
		PlayerTestHelper.equipWithItem(player, "suntan cream");

		en.step(player, "hi");
		assertEquals("Great! You got the suntan cream! Is it for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("No? Look at me! I cannot believe that you're so selfish!", getReply(npc));
		en.step(player, "task");
		assertEquals("Did you forget that you promised me to ask the #lifeguards for #'suntan cream'?", getReply(npc));
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));

		final int xp = player.getXP();
		final double karma = player.getKarma();

		en.step(player, "hi");
		assertEquals("Great! You got the suntan cream! Is it for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! I feel much better immediately! Here, take this key to my row house in Ados. Feel at home as long as I'm still here!", getReply(npc));
		// [10:03] kymara earns 1000 experience points.
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));

		assertFalse(player.isEquipped("suntan cream"));
		assertTrue(player.isEquipped("small key"));

		assertEquals(player.getQuest(questSlot), "done");

		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));

		en.step(player, "hi");
		assertEquals("Nice to meet you!", getReply(npc));
		en.step(player, "task");
		assertEquals("I don't have a new task for you. But thank you for the suntan cream. I feel my skin is getting better already!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I hope to see you soon!", getReply(npc));
		// [10:03] You see the key for Zara's row house in Ados. It is a special quest reward for kymara, and cannot be used by others.
	}
}
