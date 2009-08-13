package games.stendhal.server.maps.quests.houses;

import static games.stendhal.server.entity.npc.ConversationStates.ATTENDING;
import static games.stendhal.server.entity.npc.ConversationStates.IDLE;
import static games.stendhal.server.entity.npc.ConversationStates.QUEST_OFFERED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class AdosHouseSellerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		Chest.generateRPClass();
		Portal.generateRPClass();
		HousePortal.generateRPClass();
		MockStendlRPWorld.get();
		SingletonRepository.getNPCList().add(new SpeakerNPC("Mr Taxman"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCost() {
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		assertEquals(120000, seller.getCost());
	}

	@Test
	public void testGetLowestHouseNumber() {
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		assertEquals(50, seller.getLowestHouseNumber());

	}

	@Test
	public void testGetHighestHouseNumber() {
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		assertEquals(77, seller.getHighestHouseNumber());
		assertThat(seller.getLowestHouseNumber(), is(lessThan(seller.getHighestHouseNumber())));

	}

	@Test
	public void testAdosHouseSellerTooYoungNoQuests() {
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		Engine en = seller.getEngine();
		assertThat(en.getCurrentState(), is(IDLE));
		
		Player george = PlayerTestHelper.createPlayer("george");
		
		en.step(george, "hi");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat(seller.getText(), is("Hello, george."));
		
		en.step(george, "job");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat(seller.getText(), containsString("Ados"));
		
		en.step(george, "cost");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat("player is too young", seller.getText(), containsString("you have spent at least"));
	
		george.setAge(300 * MathHelper.MINUTES_IN_ONE_HOUR + 1);
		en.step(george, "cost");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat("player is old enough but has no quests done", seller.getText(), containsString("you must first prove yourself a worthy"));

	}
	
	@Test
	public void testAdosHouseSellerNoZones() {
		HouseUtilities.clearCache();
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		Engine en = seller.getEngine();
		en.setCurrentState(QUEST_OFFERED);
		
		
		Player george = PlayerTestHelper.createPlayer("george");
		
		en.step(george, "51");
		assertThat("no zones loaded", seller.getText(), is("Sorry I did not understand you, could you try saying the house number you want again please?"));
	}

	@Test
	public void testAdosHouseSeller() {
		
		String zoneName = "0_ados_city_n";
		StendhalRPZone ados = new StendhalRPZone(zoneName);
		MockStendlRPWorld.get().addRPZone(ados);
		HousePortal housePortal = new HousePortal("schnick bla 51");
		housePortal.setDestination(zoneName, "schnick bla 51");
		ados.add(housePortal);
		ados.add(new StoredChest());
		HouseUtilities.clearCache();
		
		
		AdosHouseSeller seller = new AdosHouseSeller("bob", "nirvana", HouseBuyingMain.houseTax);
		Engine en = seller.getEngine();
		en.setCurrentState(QUEST_OFFERED);
		
		
		Player george = PlayerTestHelper.createPlayer("george");
		
		en.step(george, "51");
		assertThat("no zones loaded", seller.getText(), is("You do not have enough money to buy a house!"));
		assertThat(en.getCurrentState(), is(ATTENDING));
		
		en.setCurrentState(QUEST_OFFERED);
		
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(120000);
		george.equipToInventoryOnly(money);
		assertFalse(george.isEquipped("house key"));
		assertTrue(george.isEquipped("money", 120000));
		en.step(george, "51");
		assertThat(seller.getText(), containsString("Congratulations"));
		assertFalse(george.isEquipped("money", 120000));
		assertTrue(george.isEquipped("house key"));
	
	}
	
}
