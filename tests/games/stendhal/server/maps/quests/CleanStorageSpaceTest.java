package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.storage.HousewifeNPC;
import marauroa.common.Log4J;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.NPCTestBase;

public class CleanStorageSpaceTest extends NPCTestBase {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();

		setupZone(ZONE_NAME, new HousewifeNPC());

		CleanStorageSpace cf = new CleanStorageSpace();
		cf.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public CleanStorageSpaceTest() {
		super(ZONE_NAME, "Eonna");
	}

	@Test
	public void testHiAndbye() {
		assertTrue(!player.hasKilled("rat"));

		SpeakerNPC npc = getNPC("Eonna");
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
		SpeakerNPC npc = getNPC("Eonna");
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
