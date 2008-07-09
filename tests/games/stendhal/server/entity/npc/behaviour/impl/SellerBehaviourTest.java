package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class SellerBehaviourTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
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
	public void testSellerBehaviour() {
		SellerBehaviour sb = new SellerBehaviour();
		assertTrue(sb.dealtItems().isEmpty());
		assertEquals(sb.amount, 0);
		assertNull(sb.chosenItemName);
		assertTrue(sb.itemNames.isEmpty());
		assertTrue(sb.priceList.isEmpty());

	}

	@Test
	public void testSellerBehaviourMapOfStringInteger() {

		Map<String, Integer> pricelist = new HashMap<String, Integer>();
		SellerBehaviour sb = new SellerBehaviour(pricelist);
		assertTrue(sb.dealtItems().isEmpty());
		assertEquals(sb.amount, 0);
		assertNull(sb.chosenItemName);
		assertTrue(sb.itemNames.isEmpty());
		assertTrue(sb.priceList.isEmpty());

		pricelist.put("item1", 10);
		pricelist.put("item2", 20);

		sb = new SellerBehaviour(pricelist);
		assertEquals(sb.dealtItems().size(), 2);
		assertTrue(sb.dealtItems().contains("item1"));
		assertTrue(sb.dealtItems().contains("item2"));
		assertEquals(sb.amount, 0);
		assertNull(sb.chosenItemName);

	}
	
	@Test
	public void testBottlesGlasses(){
		Map<String, Integer> pricelist = new HashMap<String, Integer>();
		pricelist.put("dingo",3);
		SellerBehaviour sb = new SellerBehaviour(pricelist);
		SpeakerNPC npc = new SpeakerNPC("npc");
		npc.addGreeting("blabla");
		new SellerAdder().addSeller(npc, sb);
	    Player player = PlayerTestHelper.createPlayer("bob");
	    
	    npc.getEngine().step(player, "hi");
	    npc.getEngine().step(player, "buy 1 potion");
		assertEquals("Sorry, I don't sell bottles of potion.", npc.getText());

	    npc.getEngine().step(player, "buy wine");
		assertEquals("Sorry, I don't sell glasses of wine.", npc.getText());

	    npc.getEngine().step(player, "buy 1 glass of wine");
		assertEquals("Sorry, I don't sell glasses of wine.", npc.getText());

	    npc.getEngine().step(player, "buy 1 bottle of wine");
		assertEquals("Sorry, I don't sell glasses of wine.", npc.getText());
		
	}

}
