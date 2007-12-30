package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.bar.BarMaidNPC;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test selling cheese to the bar maid.
 * @author Martin Fuchs
 */
public class SellingTest {

	private static final String ZONE_NAME = "int_ados_bar";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addRPZone(zone);

		BarMaidNPC bar = new BarMaidNPC();
		bar.configureZone(zone, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		PlayerTestHelper.resetNPC("Siandra");
		PlayerTestHelper.removePlayer("player");
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Siandra");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Siandra"));
		assertEquals("Hi!", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye bye!", npc.get("text"));
	}

	@Test
	public void testSelling() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Siandra");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi!", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?", npc.get("text"));

		assertTrue(en.step(player, "task"));
		assertEquals("Just #offers of food is enough, thank you.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I buy cheese, meat, spinach, ham, flour, and porcini.", npc.get("text"));

		assertTrue(en.step(player, "sell cheese"));
		assertEquals("1 piece of cheese is worth 5. Do you want to sell it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have any piece of cheese.", npc.get("text"));

		 // equip the player with enough cheese to be sold
		assertFalse(player.isEquipped("cheese", 1));
		assertTrue(PlayerTestHelper.equipWithStackableItem(player, "cheese", 3));
        assertTrue(player.isEquipped("cheese", 3));
        assertFalse(player.isEquipped("cheese", 4));

		assertTrue(en.step(player, "sell cheese"));
		assertEquals("1 piece of cheese is worth 5. Do you want to sell it?", npc.get("text"));

		 // ensure we currently don't have any money
		assertFalse(player.isEquipped("money", 1));

		assertTrue(en.step(player, "yes"));
		assertEquals("Thanks! Here is your money.", npc.get("text"));

		 // check if we got the promised money and the cheese is gone into Siandra's hands
		assertTrue(player.isEquipped("money", 5));
        assertTrue(player.isEquipped("cheese", 2));
        assertFalse(player.isEquipped("cheese", 3));
	}
}
