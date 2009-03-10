package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test buying ice cream.
 *
 * @author Martin Fuchs
 */
public class HouseBuyingTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_kalavan_city";
	private static final String ZONE_NAME2 = "int_ados_town_hall_3";
	private static final String ZONE_NAME3 = "int_kirdneh_townhall";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
		setupZone(ZONE_NAME2);
		setupZone(ZONE_NAME3);

		new HouseBuying().addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public HouseBuyingTest() {
		super(ZONE_NAME, "Barrett Holmes", "Reg Denson");
	}

	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		assertNotNull(npc);
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Hello, player.", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Goodbye.", npc.get("text"));
	}

	@Test
	public void testBuyHouse() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, player.", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm an estate agent. In simple terms, I sell houses for the city of Ados. Please ask about the #cost if you are interested. Our brochure is at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell Ados houses, please look at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.", npc.get("text"));

		assertTrue(en.step(player, "quest"));
		assertEquals("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses.", npc.get("text"));

		assertTrue(en.step(player, "cost"));
		assertTrue(npc.get("text").startsWith("The cost of a new house in Ados is 120000 money. But I am afraid I cannot trust you with house ownership just yet,"));

		assertTrue(en.step(player, "buy dog"));
		assertEquals("You may wish to know the #cost before you buy. Perhaps our brochure, #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses would also be of interest.", npc.get("text"));

		assertTrue(en.step(player, "buy house"));
		assertEquals("You may wish to know the #cost before you buy. Perhaps our brochure, #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses would also be of interest.", npc.get("text"));

//TODO mf - finish house buying test
//		assertTrue(en.step(player, "really"));
//		assertEquals("Sorry, I don't sell someunknownthings.", npc.get("text"));
//
//		assertTrue(en.step(player, "buy house"));
//		assertEquals("1 house will cost 30. Do you want to buy it?", npc.get("text"));
//
//		assertTrue(en.step(player, "yes"));
//		assertEquals("Sorry, you don't have enough money!", npc.get("text"));
//
//		// equip with enough money
//		assertTrue(equipWithMoney(player, 500000));
//
//		assertFalse(player.isEquipped("house"));
//
//		assertTrue(en.step(player, "yes"));
//		assertEquals("Congratulations! Here is your house!", npc.get("text"));
//		assertTrue(player.isEquipped("house", 1));
//
//		assertTrue(en.step(player, "buy house"));
//		assertEquals("1 house will cost 30. Do you want to buy it?", npc.get("text"));
	}

	@Test
	public void testReally() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Reg Denson"));
		assertEquals("Hello, player.", npc.get("text"));

		assertTrue(en.step(player, "really"));
		assertEquals("That's right, really, really, really. Really.", npc.get("text"));

		assertTrue(en.step(player, "cost"));
		assertTrue(npc.get("text").startsWith("The cost of a new house in Ados is 120000 money. But I am afraid I cannot trust you with house ownership just yet,"));
		assertFalse(en.step(player, "ok"));
	}

}
