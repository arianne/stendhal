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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test the NPC conversation WordEntry class.
 *
 * @author Martin Fuchs
 */
public class WordEntryTest {

	@Test
	public final void testWordEntry() {
		final WordEntry w = new WordEntry();
		assertEquals("/", w.getNormalizedWithTypeString());
		assertEquals("/", w.toString());

		w.setNormalized("norm");
		w.setPlurSing("plur");
		assertEquals("norm/", w.getNormalizedWithTypeString());
		assertEquals("norm/", w.toString());

		w.setType(new ExpressionType("TYP"));
		w.setValue(4711);
		assertEquals("norm/TYP", w.getNormalizedWithTypeString());
		assertEquals("norm/TYP", w.toString());
	}

}
