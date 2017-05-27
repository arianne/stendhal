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
package games.stendhal.common.parser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test the NPC conversation CompoundName class.
 *
 * @author Martin Fuchs
 */
public class CompoundNamesTest {

	/**
	 * Compound names tests.
	 */
	@Test
	public final void testCompoundNames() {
		WordList wl = WordList.getInstance();

		wl.registerName("ados city", ExpressionType.OBJECT);
		wl.registerName("ados city scroll", ExpressionType.OBJECT);

		Sentence sentence = ConversationParser.parse("I think this kobold npc up ados city");
		assertFalse(sentence.hasError());
		assertEquals(6, sentence.getExpressions().size());
		assertEquals("i/SUB-PRO think/VER this kobold/SUB-PRO npc/OBJ up/PRE ados city/OBJ", sentence.toString());

		CompoundName cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("ados").expressions, 0);
		assertNull(cn);

		cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("ados scroll").expressions, 0);
		assertNull(cn);

		cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("ados city").expressions, 0);
		assertArrayEquals(new String[]{"ados","city"}, cn.toArray());
		assertNotNull(cn);

		cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("ados city scroll").expressions, 0);
		assertArrayEquals(new String[]{"ados","city","scroll"}, cn.toArray());
		assertNotNull(cn);

		cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("buy ados city scroll").expressions, 0);
		assertNull(cn);

		cn = wl.searchCompoundName(ConversationParser.parseAsMatchingSource("buy ados city scroll").expressions, 1);
		assertArrayEquals(new String[]{"ados","city","scroll"}, cn.toArray());
		assertNotNull(cn);
	}
}
