package games.stendhal.server.entity.item;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.Attributes;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConsumableItemTest {
	private static ConsumableItem c100_1;
	private static ConsumableItem d100_1;
	private static ConsumableItem c50_1;
	private static ConsumableItem c100_2;
	private static ConsumableItem c100_3;
	private static ConsumableItem c200_1;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount","1");
		attributes.put("regen","200");
		attributes.put("frequency","1");
		c200_1= new ConsumableItem(null, null, null, attributes );
		
		
		attributes.put("regen","100");
		attributes.put("frequency","1");
		c100_1 = new ConsumableItem(null, null, null, attributes );
		assertEquals(100, c100_1.getRegen());
		assertEquals(1, c100_1.getFrecuency());
		
		attributes.put("regen","100");
		attributes.put("frequency","1");
		d100_1 = new ConsumableItem(null, null, null, attributes );
		
		attributes.put("regen","50");
		attributes.put("frequency","1");
		c50_1= new ConsumableItem(null, null, null, attributes );
		assertEquals(50, c50_1.getRegen());
		assertEquals(1, c50_1.getFrecuency());
		
		attributes.put("regen","100");
		attributes.put("frequency","2");
		c100_2= new ConsumableItem(null, null, null, attributes );

		attributes.put("regen","100");
		attributes.put("frequency","3");
		c100_3= new ConsumableItem(null, null, null, attributes );
		

		
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
	public void compareSGNxy_minSGNyx() {
		//sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
		assertTrue(c100_1.compareTo(c50_1)>0);
		assertTrue(c50_1.compareTo(c100_1)<0);
		assertTrue(Math.signum(c100_1.compareTo(c50_1))==-Math.signum(c50_1.compareTo(c100_1)));
		assertTrue(c100_2.compareTo(c100_1)<0);
		assertTrue(c100_1.compareTo(c100_2)>0);
		assertTrue(Math.signum(c100_1.compareTo(c100_2))==-Math.signum(c100_2.compareTo(c100_1)));

	}
	
	@Test
	public void comparetransient() {
//		(x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
		assertTrue(c50_1.compareTo(c100_1)<0);
		assertTrue(c100_1.compareTo(c200_1)<0);
		assertTrue(c50_1.compareTo(c200_1)<0);

		assertTrue(c200_1.compareTo(c100_1)>0);
		assertTrue(c100_1.compareTo(c50_1)>0);
		assertTrue(c200_1.compareTo(c50_1)>0);
		
	}
	@Test
	public void compare_xy_sgnxz_sgnyz() {
//	x.compareTo(y)==0  implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z)), for all z.
		assertEquals(0,c100_2.compareTo(c50_1));
		assertTrue(Math.signum(c50_1.compareTo(c100_1))
				==Math.signum(c100_2.compareTo(c100_1)));

	}
	@Test 
	public void compareTO_Equals() {
//		(x.compareTo(y)==0) == (x.equals(y)).
		// TODO: decide if we want comparision be strict for consumables
		assertEquals(0,c100_1.compareTo(d100_1));
		assertEquals(0,d100_1.compareTo(c100_1));
		assertEquals(0,c100_1.compareTo(c100_1));
	}
		

}
