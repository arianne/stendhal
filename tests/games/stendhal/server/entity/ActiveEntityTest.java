package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import games.stendhal.common.Direction;

import java.awt.Rectangle;

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
	public final void testGetDirectionTowardArea() {
		ActiveEntity ae = new ActiveEntity() {
		};
		ae.setPosition(5, 5);
		Rectangle area;

		assertEquals(5, ae.getX());
		assertEquals(5, ae.getY());
		area = new Rectangle(4, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));
		area = new Rectangle(5, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));
		area = new Rectangle(6, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));

		area = new Rectangle(4, 5, 1, 1);
		assertEquals(Direction.LEFT, ae.getDirectionToward(area));
		// ae.faceto(5,5); assertEquals(Direction.DOWN, ae.getDirection());
		area = new Rectangle(6, 5, 1, 1);
		assertEquals(Direction.RIGHT, ae.getDirectionToward(area));

		area = new Rectangle(4, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
		area = new Rectangle(5, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
		area = new Rectangle(6, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
	}

	@Test
	public final void testFaceto() {
		ActiveEntity ae = new ActiveEntity() {
		};
		ae.setPosition(5, 5);

		assertEquals(5, ae.getX());
		assertEquals(5, ae.getY());
		ae.faceto(4, 4);
		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(5, 4);
		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(6, 4);
		assertEquals(Direction.UP, ae.getDirection());

		ae.faceto(4, 5);
		assertEquals(Direction.LEFT, ae.getDirection());
		// ae.faceto(5,5); assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(6, 5);
		assertEquals(Direction.RIGHT, ae.getDirection());

		ae.faceto(4, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(5, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(6, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
	}

}
