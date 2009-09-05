package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.quests.houses.HouseUtilities;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class HouseBuyingTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_kalavan_city";
	private static final String ZONE_NAME2 = "int_ados_town_hall_3";
	private static final String ZONE_NAME3 = "int_kirdneh_townhall";
	
	private static final String[] CITY_ZONES = { 
		"0_kalavan_city",
		"0_kirdneh_city",
		"0_ados_city_n",
		"0_ados_city",
		"0_ados_city_s",
		"0_ados_wall",
		"0_athor_island"	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		HousePortal.generateRPClass();
		StoredChest.generateRPClass();

		setupZone(ZONE_NAME);
		setupZone(ZONE_NAME2);
		setupZone(ZONE_NAME3);
		
		for (String zone : CITY_ZONES) {
			setupZone(zone);
		}
		
		SpeakerNPC taxman = new SpeakerNPC("Mr Taxman");
		SingletonRepository.getNPCList().add(taxman);

		new HouseBuying().addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public HouseBuyingTest() {
		super(ZONE_NAME, "Barrett Holmes", "Reg Denson", "Mr Taxman");
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
	public void testGeneralStuff() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, player.", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm an estate agent. In simple terms, I sell houses for the city of Ados. Please ask about the #cost if you are interested. Our brochure is at #http://stendhal.game-host.org/wiki/index.php/StendhalHouses.", npc.get("text"));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell houses, please look at #http://stendhal.game-host.org/wiki/index.php/StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.", npc.get("text"));

		assertTrue(en.step(player, "quest"));
		assertEquals("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #http://stendhal.game-host.org/wiki/index.php/StendhalHouses.", npc.get("text"));
	}

	@Test
	public void testBuyHouse() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, player.", npc.get("text"));

		assertTrue(en.step(player, "cost"));
		assertTrue(npc.get("text").startsWith("The cost of a new house in Ados is 120000 money. But I am afraid I cannot trust you with house ownership just yet,"));

		player.setAge(3700000);
		assertTrue(en.step(player, "cost"));
		assertEquals("The cost of a new house in Ados is 120000 money. But I am afraid I cannot sell you a house yet as you must first prove yourself a worthy #citizen.", npc.getText());
		
		// satisfy the rest of the ados conditions
		player.setQuest("daily_item", "done");
		player.setQuest("toys_collector", "done");
		player.setQuest("hungry_joshua", "done");
		player.setQuest("find_ghosts", "done");
		player.setQuest("get_fishing_rod", "done");
		player.setQuest("suntan_cream_zara", "done");
		assertTrue(en.step(player, "buy"));
		assertEquals("The cost of a new house in Ados is 120000 money. Also, you must pay a house tax of 1000 money,"
				+ " every month. If you have a house in mind, please tell me the number now. I will check availability. "
				+ "The Ados houses are numbered from 50 to 77.", npc.getText());
		
		// add a portal to the maps so that there's something to check and sell
		Portal destination = new Portal();
		destination.setIdentifier("dest"); 
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(destination);
		StoredChest chest = new StoredChest();
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(chest);
		
		HousePortal portal = new HousePortal("ados house 50");
		portal.setDestination(ZONE_NAME, "dest");
		SingletonRepository.getRPWorld().getRPZone("0_ados_city").add(portal);
		HouseUtilities.clearCache();

		assertTrue(en.step(player, "50"));
		assertEquals("You do not have enough money to buy a house!", npc.getText());
		
		player.equip(SingletonRepository.getEntityManager().getItem("money"), 120000);
		
		// don't answer anything
		assertFalse(en.step(player, "42"));
		
		assertTrue(en.step(player, "buy"));
		assertTrue(en.step(player, "50"));
		assertEquals("Congratulations, here is your key to ados house 50! Make sure you change the locks if you ever lose it."
				+ " Do you want to buy a spare key, at a price of 1000 money?", npc.getText());

		assertTrue(player.isEquipped("player's house key"));
		
		Item item = player.getFirstEquipped("player's house key");
		assertNotNull(item);
		assertEquals("ados house 50;0;player", item.get("infostring"));
		assertFalse(item.isBound());
		
		assertTrue(en.step(player, "no"));
		assertEquals("No problem! Just so you know, if you need to #change your locks, I can do that, "
				+ "and you can also #resell your house to me if you want to.", npc.get("text"));
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
