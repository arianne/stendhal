package games.stendhal.server.entity.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

public class CellTest {

	/**
	 * Tests for cell.
	 */
	@Test
	public void testCell() {
		Point  p = new Point(2, 2);
		Cell cell = new Cell(p);
		assertSame(p, cell.getEntry());
	}

	/**
	 * Tests for remove.
	 */
	@Test
	public void testRemove() {
		Cell cell = new Cell(new Point(0, 0));
		assertTrue(cell.isEmpty());
		assertFalse(cell.remove("jack"));
		assertTrue(cell.isEmpty());
	}
	/**
	 * Tests for add.
	 */
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
