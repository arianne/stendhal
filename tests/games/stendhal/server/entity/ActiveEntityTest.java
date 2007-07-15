package games.stendhal.server.entity;

import static org.junit.Assert.*;
import games.stendhal.common.Direction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActiveEntityTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetDirectionTowardDoubleDouble() {
		ActiveEntity ae = new ActiveEntity(){};
		ae.set(5,5);
		
		assertEquals(5,ae.getX());
		assertEquals(5,ae.getY());
		
		assertEquals(Direction.UP, ae.getDirectionToward(4,4));
		assertEquals(Direction.LEFT, ae.getDirectionToward(4,5));
		assertEquals(Direction.LEFT, ae.getDirectionToward(4,6));
		assertEquals(Direction.UP, ae.getDirectionToward(5,4));
		assertEquals(Direction.UP, ae.getDirectionToward(5,5));
		assertEquals(Direction.DOWN, ae.getDirectionToward(5,6));
		assertEquals(Direction.UP, ae.getDirectionToward(6,4));
		assertEquals(Direction.UP, ae.getDirectionToward(6,5));
		assertEquals(Direction.DOWN, ae.getDirectionToward(6,6));
	}

	@Test
	public final void testFaceto() {
		ActiveEntity ae = new ActiveEntity(){};
		ae.set(5,5);
		
		assertEquals(5,ae.getX());
		assertEquals(5,ae.getY());
		ae.faceto(4,4);		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(4,5);		assertEquals(Direction.LEFT, ae.getDirection());
		ae.faceto(4,6);		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(5,4);		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(5,5);		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(5,6);		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(6,4);		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(6,5);		assertEquals(Direction.RIGHT, ae.getDirection());
		ae.faceto(6,6);		assertEquals(Direction.DOWN, ae.getDirection());
	}

}
