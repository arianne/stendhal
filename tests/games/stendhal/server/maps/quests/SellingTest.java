package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.bar.BarMaidNPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTest;

/**
 * Test selling cheese to the bar maid.
 *
 * @author Martin Fuchs
 */
public class SellingTest extends ZonePlayerAndNPCTest {

	private static final String ZONE_NAME = "int_ados_bar";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ZonePlayerAndNPCTest.setUpBeforeClass();

		setupZone(ZONE_NAME, new BarMaidNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public SellingTest() {
		super(ZONE_NAME, "Siandra");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Siandra");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Siandra"));
		assertEquals("Hi!", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye bye!", npc.get("text"));
	}

	@Test
	public void testSelling() {
		SpeakerNPC npc = getNPC("Siandra");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi!", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?", npc.get("text"));

		assertTrue(en.step(player, "task"));
		assertEquals("Just #offers of food is enough, thank you.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I buy cheese, meat, spinach, ham, flour, and porcini.", npc.get("text"));

		assertTrue(en.step(player, "sell"));
		assertEquals("Please tell me what you want to sell.", npc.get("text"));

		assertTrue(en.step(player, "sell house"));
		assertEquals("Sorry, I don't buy any houses.", npc.get("text"));

		assertTrue(en.step(player, "sell cheese"));
		assertEquals("1 piece of cheese is worth 5. Do you want to sell it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry! You don't have any piece of cheese.", npc.get("text"));

		 // equip the player with enough cheese to be sold
		assertFalse(player.isEquipped("cheese", 1));
		assertTrue(equipWithStackableItem(player, "cheese", 3));
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
