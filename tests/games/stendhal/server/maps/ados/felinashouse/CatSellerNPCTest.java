package games.stendhal.server.maps.ados.felinashouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CatTestHelper;

/**
 * Test buying cats.
 * @author Martin Fuchs
 */
public class CatSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		
		CatTestHelper.generateRPClasses();
		ZonePlayerAndNPCTestImpl.setUpBeforeClass();

		setupZone(ZONE_NAME, new CatSellerNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public CatSellerNPCTest() {
		super(ZONE_NAME, "Felina");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Felina");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Felina"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testBuyCat() {
		SpeakerNPC npc = getNPC("Felina");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", npc.get("text"));

		assertTrue(en.step(player, "care"));
		assertEquals("Cats love chicken and fish. Just place a piece on the ground and your cat will run over to eat it. You can right-click on her and choose 'Look' at any time, to check up on her weight; she will gain one unit of weight for every piece of chicken she eats.", npc.get("text"));

		// There is currently no quest response defined for Felina.
		assertFalse(en.step(player, "quest"));

		assertTrue(en.step(player, "buy"));
		assertEquals("Please tell me what you want to buy.", npc.get("text"));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", npc.get("text"));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", npc.get("text"));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", npc.get("text"));

		assertTrue(en.step(player, "buy a glass of wine"));
		assertEquals("Sorry, I don't sell glasses of wine.", npc.get("text"));

		assertTrue(en.step(player, "buy a hand full of peace"));
		assertEquals("Sorry, I don't sell hand fulls of peace.", npc.get("text"));

		assertTrue(en.step(player, "buy cat"));
		assertEquals("1 cat will cost 100. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "buy cat"));
		assertEquals("1 cat will cost 100. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("You don't seem to have enough money.", npc.get("text"));

		assertTrue(en.step(player, "buy two cats"));
		assertEquals("2 cats will cost 200. Do you want to buy them?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hmm... I just don't think you're cut out for taking care of more than one cat at once.", npc.get("text"));

		// equip with enough money to buy the cat
		assertTrue(equipWithMoney(player, 500));
		assertTrue(en.step(player, "buy cat"));
		assertEquals("1 cat will cost 100. Do you want to buy it?", npc.get("text"));

		assertFalse(player.hasPet());

		assertTrue(en.step(player, "yes"));
		assertEquals("Here you go, a cute little kitten! Your kitten will eat any piece of chicken or fish you place on the ground. Enjoy her!", npc.get("text"));

		assertTrue(player.hasPet());
	}

	@Test
	public void testSellCat() {
		SpeakerNPC npc = getNPC("Felina");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "sell cat"));
		assertEquals("Sell??? What kind of a monster are you? Why would you ever sell your beautiful cat?", npc.get("text"));
	}

}
