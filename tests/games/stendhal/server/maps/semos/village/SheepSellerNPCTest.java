package games.stendhal.server.maps.semos.village;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test buying sheep.
 *
 * @author Martin Fuchs
 */
public class SheepSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_semos_village_w";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		ZonePlayerAndNPCTestImpl.setUpBeforeClass();

		setupZone(ZONE_NAME, new SheepSellerNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public SheepSellerNPCTest() {
		super(ZONE_NAME, "Nishiya");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Nishiya");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testBuySheep() {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		registerPlayer(player, world.getZone(ZONE_NAME));

		SpeakerNPC npc = getNPC("Nishiya");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I work as a sheep seller.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell sheep.", npc.get("text"));

		assertTrue(en.step(player, "buy"));
		assertEquals("Please tell me what you want to buy.", npc.get("text"));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("Sorry, I don't sell dogs.", npc.get("text"));

		assertTrue(en.step(player, "buy house"));
		assertEquals("Sorry, I don't sell houses.", npc.get("text"));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", npc.get("text"));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("1 sheep will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("1 sheep will cost 30. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("You don't seem to have enough money.", npc.get("text"));

		// equip with enough money to buy one sheep
		assertTrue(equipWithMoney(player, 30));

		assertTrue(en.step(player, "buy 2 sheep"));
		assertEquals("2 sheep will cost 60. Do you want to buy them?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hmm... I just don't think you're cut out for taking care of a whole flock of sheep at once.", npc.get("text"));

		assertTrue(en.step(player, "buy sheep"));
		assertEquals("1 sheep will cost 30. Do you want to buy it?", npc.get("text"));

		assertFalse(player.hasSheep());

		assertTrue(en.step(player, "yes"));
		assertEquals("Here you go, a nice fluffy little sheep! Take good care of it, now...", npc.get("text"));

		assertTrue(player.hasSheep());
	}

	@Test
	public void testSellSheep() {
		SpeakerNPC npc = getNPC("Nishiya");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Nishiya"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "sell"));
		assertEquals("Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.", npc.get("text"));

		assertTrue(en.step(player, "sell sheep"));
		assertEquals("Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.", npc.get("text"));
	}

}
