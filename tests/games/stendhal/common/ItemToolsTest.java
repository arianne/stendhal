package games.stendhal.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test the ItemTools class.
 *
 * @author Martin Fuchs
 */
public class ItemToolsTest {

	@Test
	public void testUnderscoreConversion() {
		assertEquals(null, ItemTools.itemNameToDisplayName(null));
		assertEquals("", ItemTools.itemNameToDisplayName(""));
		assertEquals(" ", ItemTools.itemNameToDisplayName(" "));
		assertEquals(" ", ItemTools.itemNameToDisplayName("_"));
		assertEquals("x ", ItemTools.itemNameToDisplayName("x_"));
		assertEquals(" x", ItemTools.itemNameToDisplayName("_x"));
		assertEquals("abc 1", ItemTools.itemNameToDisplayName("abc_1"));
		assertEquals("abc def", ItemTools.itemNameToDisplayName("abc def"));
		assertEquals("abc def ghi", ItemTools.itemNameToDisplayName("abc_def ghi"));
		assertEquals("abc def ghi", ItemTools.itemNameToDisplayName("abc_def_ghi"));
		assertEquals("abc def ghi", ItemTools.itemNameToDisplayName("abc def ghi"));
		assertEquals("chicken", ItemTools.itemNameToDisplayName("chicken"));
	}

}
