package games.stendhal.server.maps.quests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.magic.school.GroundskeeperNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class KillSpidersTest {


	private static String questSlot = "kill_all_spiders";
	
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new GroundskeeperNPC().configureZone(zone, null);
		
			
		final AbstractQuest quest = new KillSpiders();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		final double oldkarma = player.getKarma();
		
		npc = SingletonRepository.getNPCList().get("Morgrin");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine fine, I hope you enjoy your day.", getReply(npc));
		en.step(player, "task");
		assertEquals("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. Go down to the basement and kill all the creatures there!", getReply(npc));
		assertThat(player.getKarma(), greaterThan(oldkarma));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("Go down and kill the creatures, no time left.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		player.setSharedKill("poisonous spider");
		player.setSharedKill("spider");
		player.setSharedKill("giant spider");
		
		final int xp = player.getXP();
		final double karma = player.getKarma();
		
		en.step(player, "hi");
		// [15:13] kymara earns 5000 experience points.
		assertEquals("Oh thank you my friend. Here you have something special, I got it from a Magican. Who he was I do not know. What the egg's good for, I do not know. I only know, it could be useful for you.", getReply(npc));
		assertTrue(player.isEquipped("mythical egg"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));
		assertTrue(player.getQuest(questSlot).startsWith("killed"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Sorry there is nothing to do for you yet. But maybe you could come back later. I have to clean the school once a week.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		final double newKarma = player.getKarma();
		
		// [15:14] Changed the state of quest 'kill_all_spiders' from 'killed;1219677211115' to 'killed;0'
		player.setQuest(questSlot, "killed;0");
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Would you like to help me again?", getReply(npc));
		en.step(player, "no");
		assertThat(player.getKarma(), lessThan(newKarma));
		assertEquals("Ok, I have to find someone else to do this 'little' job!", getReply(npc));
		assertThat(player.getQuest(questSlot), is("rejected"));
		en.step(player, "bye");
		
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. Go down to the basement and kill all the creatures there!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
