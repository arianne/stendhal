package games.stendhal.server.entity.creature.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AttackStrategyFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testGet() {
		Map<String , String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("archer", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof RangeAttack);
		
		
	}

}
