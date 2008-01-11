package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class DirectionTest {

	@Test
	public final void testBuild() {
		assertSame(Direction.STOP, Direction.build(0));
		assertSame(Direction.UP, Direction.build(1));
		assertSame(Direction.RIGHT, Direction.build(2));
		assertSame(Direction.DOWN, Direction.build(3));
		assertSame(Direction.LEFT, Direction.build(4));
	}

	@Test
	public final void testGetdx() {
		assertEquals(0, Direction.STOP.getdx());
		assertEquals(0, Direction.UP.getdx());
		assertEquals(0, Direction.DOWN.getdx());

		assertEquals(1, Direction.RIGHT.getdx());
		assertEquals(-1, Direction.LEFT.getdx());
	}

	@Test
	public final void testGetdy() {
		assertEquals(0, Direction.STOP.getdy());
		assertEquals(0, Direction.RIGHT.getdy());
		assertEquals(0, Direction.LEFT.getdy());

		assertEquals(-1, Direction.UP.getdy());
		assertEquals(1, Direction.DOWN.getdy());
	}

	@Test
	public final void testGet() {
		assertEquals(0, Direction.STOP.get());
		assertEquals(1, Direction.UP.get());
		assertEquals(2, Direction.RIGHT.get());
		assertEquals(3, Direction.DOWN.get());
		assertEquals(4, Direction.LEFT.get());
	}

	@Test
	public final void testOppositeDirection() {
		assertEquals(Direction.UP, Direction.DOWN.oppositeDirection());
		assertEquals(Direction.DOWN, Direction.UP.oppositeDirection());
		assertEquals(Direction.LEFT, Direction.RIGHT.oppositeDirection());
		assertEquals(Direction.RIGHT, Direction.LEFT.oppositeDirection());
		assertEquals(Direction.STOP, Direction.STOP.oppositeDirection());
		assertEquals(Direction.UP, Direction.UP.oppositeDirection()
				.oppositeDirection());
	}

}
