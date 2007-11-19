package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class WizardBankTest {

	private static final String GRAFINDLE_QUEST_SLOT = "grafindle_gold";
	private static final String ZARA_QUEST_SLOT = "suntan_cream_zara";
	private static final String ZONE_NAME = "int_magic_bank";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();

		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addRPZone(zone);

		WizardBank wb = new WizardBank();
		wb.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SpeakerNPC npc = NPCList.get().get("Javier X");
		if (npc != null) {
			npc.setCurrentState(ConversationStates.IDLE);
		}
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer();

		SpeakerNPC npc = NPCList.get().get("Javier X");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				npc.get("text"));

		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				npc.get("text"));

		player.setQuest(ZARA_QUEST_SLOT, "done");
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("Welcome to the Wizard's Bank, player.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));
	}

	@Test
	public void testDoQuest() {
	/*
		Player player = PlayerTestHelper.createPlayer();

		SpeakerNPC npc = NPCList.get().get("Javier X");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		//TODO

	*/
	}
}
