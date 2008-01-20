package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.storage.HousewifeNPC;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CleanStorageSpaceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		HousewifeNPC eonna = new HousewifeNPC();
		eonna.configureZone(new StendhalRPZone("testzone"), null);

		CleanStorageSpace cf = new CleanStorageSpace();
		cf.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		PlayerTestHelper.resetNPC("Eonna");
	}

	@Test
	public void testHiAndbye() {
		Player player;
		player = PlayerTestHelper.createPlayer("player");

		assertTrue(!player.hasKilled("rat"));

		SpeakerNPC npc = NPCList.get().get("Eonna");
		assertNotNull(npc);
		Engine en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi there, young hero.", npc.get("text"));
		assertTrue(en.step(player, "job"));
		assertTrue(npc.isTalking());
		assertEquals("I'm just a regular housewife.", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertTrue(npc.isTalking());
		assertEquals("I don't think I can help you with anything.",
				npc.get("text"));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void doQuest() {
		Player player = PlayerTestHelper.createPlayer("player");



		SpeakerNPC npc = NPCList.get().get("Eonna");
		assertNotNull(npc);
		Engine en = npc.getEngine();
		assertFalse(npc.isTalking());
		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi there, young hero.", npc.get("text"));
		assertTrue(en.step(player, "task"));
		assertTrue(npc.isTalking());
		assertEquals(
				"My #basement is absolutely crawling with rats. Will you help me?",
				npc.get("text"));
		assertTrue(en.step(player, "basement"));
		assertTrue(npc.isTalking());
		assertEquals(
				"Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?",
				npc.get("text"));
		assertTrue(en.step(player, "yes"));
		assertEquals(
				"Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!",
				npc.get("text"));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
		player.setSoloKill("rat");
		assertTrue(player.hasKilled("rat"));
		player.setSharedKill("caverat");
		player.setSharedKill("snake");
		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("A hero at last! Thank you!", npc.get("text"));

		assertEquals("done", player.getQuest("clean_storage"));
	}

}
