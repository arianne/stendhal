package games.stendhal.client.gui.bag;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CircledCollectionTest {

	/**
	 * Tests for add.
	 */
	@Test
	public void testAdd() throws Exception {
		CircledCollection<String> col = new CircledCollection<String>();
		String testString = "bob";
		String testString2 = "bob2";
		col.add(testString);
		assertEquals(1, col.size());
	
		col.add(testString2);
		assertEquals(2, col.size());
		
	}
	
	/**
	 * Tests for moveNext.
	 */
	@Test
	public void testMoveNext() throws Exception {
		CircledCollection<String> col = new CircledCollection<String>();
		String headString1 = "1";
		String testString2 = "2";
		String testString3 = "3";
		
		col.add(headString1);
		col.add(testString2);
		col.add(testString3);
		
		assertSame(headString1, col.getCurrent());
		assertTrue(col.moveNext());
		assertSame(testString2, col.getCurrent());
		assertTrue(col.moveNext());
		assertSame(testString3, col.getCurrent());
		assertFalse(col.moveNext());
		assertSame(headString1, col.getCurrent());
		
	}
}
