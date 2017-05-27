package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
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
import games.stendhal.server.maps.semos.yeticave.MrYetiNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class SnowballsTest {

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
		new MrYetiNPC().configureZone(zone, null);


		AbstractQuest quest = new Snowballs();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Mr. Yeti");
		en = npc.getEngine();


		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings stranger! Have you seen my snow sculptures? I need a #favor from someone friendly like you.", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "no");
		assertEquals("So what are you doing here? Go away!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertEquals(player.getQuest(questSlot), "rejected");

		en.step(player, "hi");
		assertEquals("Greetings stranger! Have you seen my snow sculptures? I need a #favor from someone friendly like you.", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertEquals(player.getQuest(questSlot), "start");

		en.step(player, "hi");
		assertEquals("You're back already? Don't forget that you promised to collect a bunch of snowballs for me!", getReply(npc));
		en.step(player, "quest");
		assertEquals("You already promised me to bring some snowballs! Twenty five pieces, remember ...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "snowball", 25);
		assertTrue(player.isEquipped("snowball", 25));

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh I hope you bring me them soon! I would like to finish my sculpture!", getReply(npc));
		en.step(player, "quest");
		assertEquals("You already promised me to bring some snowballs! Twenty five pieces, remember ...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		// put the snowballs on the ground
		player.drop("snowball", 25);
		assertFalse(player.isEquipped("snowball", 25));

		en.step(player, "yes");
		assertEquals("Hey! Where did you put the snowballs?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "snowball", 25);

		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		en.step(player, "yes");
		assertThat(getReply(npc), is(oneOf("Thank you! Here, take some perch! I do not like to eat them.", "Thank you! Here, take some cod! I do not like to eat them.")));
		// [09:49] kymara earns 50 experience points.
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertThat(player.getXP(), greaterThan(xp));
		assertFalse(player.isEquipped("snowballs", 25));
		assertTrue(player.isEquipped("perch", 20) || player.isEquipped("cod", 20) );

		assertNotNull(player.getQuest(questSlot));
		assertFalse(player.getQuest(questSlot).equals("start"));

		en.step(player, "hi");
		assertEquals("I have enough snow for my new sculpture. Thank you for helping! I might start a new one in 2 hours.", getReply(npc));
		en.step(player, "task");
		assertEquals("I have enough snow to finish my sculpture, but thanks for asking.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// [09:49] Admin kymara changed your state of the quest 'snowballs' from '1288518569387' to '0'
		// [09:49] Changed the state of quest 'snowballs' from '1288518569387' to '0'

		player.setQuest(questSlot, "0");

		en.step(player, "hi");
		assertEquals("Greetings again! Have you seen my latest snow sculptures? I need a #favor again ...", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
