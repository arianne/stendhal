package games.stendhal.server.maps.semos.kanmararn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.JailedDwarf;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test for DwarfGuardNPC: sell chaos legs
 *
 * @author Martin Fuchs
 */
public class DwarfGuardNPCTest {

	private static final String ZONE_NAME = "-7_kanmararn_prison";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addRPZone(zone);

		ZoneConfigurator zoneConf = new DwarfGuardNPC();
		zoneConf.configureZone(new StendhalRPZone(ZONE_NAME), null);

		JailedDwarf quest = new JailedDwarf();
		quest.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		PlayerTestHelper.resetNPC("Hunel");
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Hunel");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Hunel"));
		assertEquals("Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles.", npc.get("text"));

		// Hunel doesn't listen to us until we get the prison key.
		assertFalse(en.step(player, "bye"));

		PlayerTestHelper.equipWithItem(player, "kanmararn prison key");

		assertTrue(en.step(player, "hi Hunel"));
		assertEquals("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye .. be careful ..", npc.get("text"));
	}

	@Test
	public void testQuest() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Hunel");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		PlayerTestHelper.equipWithItem(player, "kanmararn prison key");

		assertTrue(en.step(player, "hi"));
		assertEquals("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm was the guard of this Prison. Until .. well you know the rest.", npc.get("text"));

		assertTrue(en.step(player, "task"));
		assertEquals("I'm too scared to leave here yet... can you offer me some really good equipment?", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I buy chaos legs, chaos sword, chaos shield, and chaos armor.", npc.get("text"));

		assertTrue(en.step(player, "sell chocolate"));
		assertEquals("Sorry, I don't buy any chocolates.", npc.get("text"));

		assertTrue(en.step(player, "sell chaos legs"));
		assertEquals("1 pair of chaos legs is worth 8000. Do you want to sell it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, then how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "sell two chaos legs"));
		assertEquals("2 pairs of chaos legs are worth 16000. Do you want to sell them?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have that many pairs of chaos legs.", npc.get("text"));

		assertTrue(PlayerTestHelper.equipWithItem(player, "chaos legs"));
		assertTrue(en.step(player, "sell chaos leg"));
		assertEquals("1 pair of chaos legs is worth 8000. Do you want to sell it?", npc.get("text"));

		assertFalse(player.isEquipped("money", 8000));
		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", npc.get("text"));
		assertTrue(player.isEquipped("money", 8000));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye .. be careful ..", npc.get("text"));
	}
}
