package games.stendhal.server.maps.quests.houses;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BuyHouseChatActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		Chest.generateRPClass();
		Portal.generateRPClass();
		HousePortal.generateRPClass();
		MockStendlRPWorld.get();
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFire() {
		BuyHouseChatAction action = new BuyHouseChatAction(1, HouseSellerNPCBase.QUEST_SLOT);
		String zoneName = "0_ados_city_n";
		StendhalRPZone ados = new StendhalRPZone(zoneName);
		MockStendlRPWorld.get().addRPZone(ados);
		HousePortal housePortal = new HousePortal("schnick bla 51");
		housePortal.setDestination(zoneName, "schnick bla 51");
		ados.add(housePortal);
		ados.add(new StoredChest());
		HouseUtilities.allHousePortals = null;
		
		SpeakerNPC engine = new SpeakerNPC("bob");
		Player player = PlayerTestHelper.createPlayer("george");
		Sentence sentence = ConversationParser.parse("51");
		action.fire(player , sentence , engine);
		assertThat(engine.getText(), is("You do not have enough money to buy a house!"));
		housePortal.setOwner("jim");
		
		action.fire(player , sentence , engine);
		assertThat(engine.getText(), containsString("Sorry, house 51 is sold"));
		
		PlayerTestHelper.equipWithMoney(player, 1);
	
		housePortal.setOwner("");
		
		action.fire(player , sentence , engine);
		assertThat(engine.getText(), containsString("Congratulation"));
		assertFalse(player.isEquipped("money"));
		
	}

}
