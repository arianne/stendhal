package games.stendhal.server.maps.kalavan.citygardens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Test buying ice cream.
 * @author Martin Fuchs
 */
public class IceCreamSellerNPCTest {

	private static final String ZONE_NAME = "0_kalavan_city_gardens";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addRPZone(zone);

		new IceCreamSellerNPC().configureZone(zone, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SpeakerNPC npc = NPCList.get().get("Sam");
		if (npc != null) {
			npc.setCurrentState(ConversationStates.IDLE);
		}
	}

	@Test
	public void testHiAndBye() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Sam");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Hi. Can I #offer you an icecream?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye, enjoy your day!", npc.get("text"));
	}

	@Test
	public void testBuyIceCream() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Sam");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi. Can I #offer you an icecream?", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell delicious icecreams.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell icecream.", npc.get("text"));

		assertTrue(en.step(player, "quest"));
		assertEquals("Mine's a simple life, I don't need a lot.", npc.get("text"));

		assertTrue(en.step(player, "buy"));
		assertEquals("Please tell me what you want to buy.", npc.get("text"));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", npc.get("text"));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", npc.get("text"));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", npc.get("text"));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", npc.get("text"));

		// equip with enough money to buy two ice creams
		assertTrue(PlayerTestHelper.equipWithMoney(player, 60));

		assertTrue(en.step(player, "buy three icecreams"));
		assertEquals("3 icecreams will cost 90. Do you want to buy them?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", npc.get("text"));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertFalse(player.isEquipped("icecream"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here is your icecream!", npc.get("text"));
		assertTrue(player.isEquipped("icecream", 1));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here is your icecream!", npc.get("text"));
		assertTrue(player.isEquipped("icecream", 2));
	}

	@Test
	public void testSellIceCream() {
		Player player = PlayerTestHelper.createPlayer("player");

		SpeakerNPC npc = NPCList.get().get("Sam");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Sam"));
		assertEquals("Hi. Can I #offer you an icecream?", npc.get("text"));

		// Currently there are no response to sell sentences for Sam.
		assertFalse(en.step(player, "sell"));
	}
}
