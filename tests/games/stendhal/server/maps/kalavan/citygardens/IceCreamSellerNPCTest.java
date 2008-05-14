package games.stendhal.server.maps.kalavan.citygardens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test buying ice cream.
 *
 * @author Martin Fuchs
 */
public class IceCreamSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_kalavan_city_gardens";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ZonePlayerAndNPCTestImpl.setUpBeforeClass();

		setupZone(ZONE_NAME, new IceCreamSellerNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public IceCreamSellerNPCTest() {
		super(ZONE_NAME, "Sam");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Sam");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Hi. Can I #offer you an icecream?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye, enjoy your day!", npc.get("text"));
	}

	@Test
	public void testBuyIceCream() {
		SpeakerNPC npc = getNPC("Sam");
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

		assertTrue(en.step(player, "buy a bunch of socks"));
		assertEquals("Sorry, I don't sell bunches of socks.", npc.get("text"));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "buy icecream"));
		assertEquals("1 icecream will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", npc.get("text"));

		// equip with enough money to buy two ice creams
		assertTrue(equipWithMoney(player, 60));

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

		assertTrue(en.step(player, "buy 0 icecreams"));
		assertEquals("Sorry, how many icecreams do you want to buy?!", npc.get("text"));
		assertFalse(en.step(player, "yes"));
	}

	@Test
	public void testSellIceCream() {
		SpeakerNPC npc = getNPC("Sam");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Sam"));
		assertEquals("Hi. Can I #offer you an icecream?", npc.get("text"));

		// Currently there are no response to sell sentences for Sam.
		assertFalse(en.step(player, "sell"));
	}

}
