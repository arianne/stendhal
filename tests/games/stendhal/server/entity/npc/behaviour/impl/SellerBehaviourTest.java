package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

}
