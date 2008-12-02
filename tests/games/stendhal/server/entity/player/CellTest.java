package games.stendhal.server.entity.player;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CellTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCell() {
		Point  p = new Point(2, 2);
		Cell cell = new Cell(p);
		assertSame(p, cell.getEntry());
	}

	@Test
	public void testRemove() {
		Cell cell = new Cell(new Point(0, 0));
		assertTrue(cell.isEmpty());
		assertFalse(cell.remove("jack"));
		assertTrue(cell.isEmpty());
	}
	@Test
	public void testAdd() {
		Cell cell = new Cell(new Point(0, 0));
		assertTrue(cell.isEmpty());
		assertTrue(cell.add("jack"));
		assertFalse(cell.isEmpty());
		assertFalse(cell.remove("bob"));
		assertFalse(cell.isEmpty());
		assertTrue(cell.remove("jack"));
		assertTrue(cell.isEmpty());
	}

}
