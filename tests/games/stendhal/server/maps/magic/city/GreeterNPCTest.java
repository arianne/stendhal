package games.stendhal.server.maps.magic.city;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTest;

/**
 * Test buying scrolls.
 *
 * @author Martin Fuchs
 */
public class GreeterNPCTest extends ZonePlayerAndNPCTest {

	private static final String ZONE_NAME = "-1_fado_great_cave_e3";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ZonePlayerAndNPCTest.setUpBeforeClass();

		setupZone(ZONE_NAME, new GreeterNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public GreeterNPCTest() {
		super(ZONE_NAME, "Erodel Bmud");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Erodel Bmud");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Erodel"));
		assertEquals("Salutations, traveller.", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Adieu.", npc.get("text"));
	}

	@Test
	public void testBuyScroll() {
		SpeakerNPC npc = getNPC("Erodel Bmud");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Salutations, traveller.", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I am a wizard, like all who dwell in this magic underground city. We practise #magic here.", npc.get("text"));

		assertTrue(en.step(player, "magic"));
		assertEquals("Indeed, enchantments such as our Sunlight Spell to keep the grass and flowers healthy down here. I suppose you are wondering why you have seen traditional enemies such as dark elves and green elves in company together here, let me #explain.", npc.get("text"));

		assertTrue(en.step(player, "explain"));
		assertEquals("As a city for wizards only, we have much to learn from one another. Thus, old quarrels are forgotten and we live here in peace.", npc.get("text"));

		assertTrue(en.step(player, "quest"));
		assertEquals("Neither can live while the other survives! The Dark Lord must be killed...no ... wait... that was some other time. Forgive me for confusing you, I need nothing.", npc.get("text"));

		assertTrue(en.step(player, "buy"));
		assertEquals("Please tell me what you want to buy.", npc.get("text"));

		assertTrue(en.step(player, "buy cat"));
		assertEquals("Sorry, I don't sell cats.", npc.get("text"));

		assertTrue(en.step(player, "buy someunknownthing"));
		assertEquals("Sorry, I don't sell someunknownthings.", npc.get("text"));

		assertTrue(en.step(player, "buy a bottle of wine"));
		assertEquals("Sorry, I don't sell bottles of wine.", npc.get("text"));

		assertTrue(en.step(player, "buy scroll"));
		assertEquals("Sorry, I don't sell scrolls.", npc.get("text"));

		assertTrue(en.step(player, "buy summon scroll"));
		assertEquals("1 summon scroll will cost 300. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "no"));
		assertEquals("Ok, how else may I help you?", npc.get("text"));

		assertTrue(en.step(player, "buy summon scroll"));
		assertEquals("1 summon scroll will cost 300. Do you want to buy it?", npc.get("text"));

		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", npc.get("text"));

		assertTrue(en.step(player, "buy two summon scrolls"));
		assertEquals("2 summon scrolls will cost 600. Do you want to buy them?", npc.get("text"));

		// equip with enough money to buy the two scrolls
		assertTrue(equipWithMoney(player, 600));

		assertFalse(player.isEquipped("summon scroll"));
		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your summon scrolls!", npc.get("text"));
		assertTrue(player.isEquipped("summon scroll"));

		assertTrue(en.step(player, "buy home scroll"));
		assertEquals("1 home scroll will cost 375. Do you want to buy it?", npc.get("text"));

		assertTrue(equipWithMoney(player, 300));
		assertTrue(en.step(player, "yes"));
		assertEquals("Sorry, you don't have enough money!", npc.get("text"));

		assertTrue(en.step(player, "buy home scroll"));
		assertEquals("1 home scroll will cost 375. Do you want to buy it?", npc.get("text"));

		// add another 75 coins to be able to buy the scroll
		assertTrue(equipWithMoney(player, 75));

		assertFalse(player.isEquipped("home scroll"));
		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here is your home scroll!", npc.get("text"));
		assertTrue(player.isEquipped("home scroll"));
	}

	@Test
	public void testSellScroll() {
		SpeakerNPC npc = getNPC("Erodel Bmud");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Salutations, traveller.", npc.get("text"));

		// There is not yet a trigger for selling things to Erodel
		assertFalse(en.step(player, "sell summon scroll"));
	}

}
