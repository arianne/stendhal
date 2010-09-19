/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
