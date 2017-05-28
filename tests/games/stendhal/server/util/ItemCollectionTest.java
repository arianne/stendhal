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
package games.stendhal.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import games.stendhal.server.entity.npc.ConversationPhrases;

/**
 * Tests for the area class.
 *
 * @author M. Fuchs
 */
public class ItemCollectionTest {

	@Test
	public void testCreateArea() {
	    final ItemCollection coll = new ItemCollection();
	    assertEquals("", coll.toStringForQuestState());
        assertEquals(ConversationPhrases.EMPTY, coll.toStringList());

	    coll.addItem("cheese", 5);
	    assertEquals("cheese=5", coll.toStringForQuestState());

	    coll.addFromQuestStateString("cheese=2;ham=3");
        assertEquals("cheese=7;ham=3", coll.toStringForQuestState());

        assertTrue(coll.removeItem("cheese", 1));
        assertEquals("cheese=6;ham=3", coll.toStringForQuestState());
        assertEquals(Arrays.asList("6 pieces of cheese", "3 pieces of ham"), coll.toStringList());
        assertEquals(Arrays.asList("6 #'pieces of cheese'", "3 #'pieces of ham'"), coll.toStringListWithHash());

        assertFalse(coll.removeItem("ham", 5));
        assertEquals("cheese=6;ham=3", coll.toStringForQuestState());

        assertTrue(coll.removeItem("cheese", 6));
        assertEquals("ham=3", coll.toStringForQuestState());

        assertTrue(coll.removeItem("ham", 3));
        assertEquals("", coll.toStringForQuestState());

        coll.addItem("shadow legs", 1);
        assertEquals("shadow legs=1",coll.toStringForQuestState());
        assertTrue(coll.removeItem("shadow legs",1));
        assertEquals("", coll.toStringForQuestState());
	}

	@Test
	public void testAddFromQuestStateString() {
		final ItemCollection coll = new ItemCollection();
		assertEquals("", coll.toStringForQuestState());
		coll.addFromQuestStateString("cheese=6;ham=3;shadow legs=1");
		assertEquals("cheese=6;ham=3;shadow legs=1", coll.toStringForQuestState());
		assertTrue(coll.removeItem("shadow legs",1));
		assertEquals("cheese=6;ham=3", coll.toStringForQuestState());
	}

}
