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
package utilities.RPClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;

public class ItemTestHelperTest {

	@org.junit.Test
	public void testcreateItem() throws Exception {
		ItemTestHelper.createItem();
		final Item item = ItemTestHelper.createItem("blabla");
		assertEquals("blabla", item.getName());

	}

	@Test
	public void testGenerateRPClasses() {
		ItemTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("item"));

	}
}
