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
 * Test the NPC conversation PunctuationParser class.
 *
 * @author Martin Fuchs
 */
public class PunctuationParserTest {

	@Test
	public final void testWordEntry() {
		PunctuationParser p = new PunctuationParser("... hello?");
		assertEquals(" hello", p.getText());
		assertEquals("...", p.getPrecedingPunctuation());
		assertEquals("?", p.getTrailingPunctuation());

		p = new PunctuationParser("hello world !");
		assertEquals("hello world ", p.getText());
		assertEquals("", p.getPrecedingPunctuation());
		assertEquals("!", p.getTrailingPunctuation());
	}

	@Test
	public final void tesNullEntry() {
		final PunctuationParser p = new PunctuationParser(null);
		assertEquals(null, p.getText());
		assertEquals("", p.getPrecedingPunctuation());
		assertEquals("", p.getTrailingPunctuation());
	}

	/**
	 * Tests for onlyPunctuation.
	 */
	@Test
	public final void testOnlyPunctuation() {
		final String s = ".,?!";
		final PunctuationParser p = new PunctuationParser(s);
		assertEquals("", p.getText());
		assertEquals(".,?!", p.getPrecedingPunctuation());
		assertEquals("", p.getTrailingPunctuation());
	}

	/**
	 * Tests for emptyString.
	 */
	@Test
	public final void testEmptyString() {
		final PunctuationParser p = new PunctuationParser("");
		assertEquals("", p.getText());
		assertEquals("", p.getPrecedingPunctuation());
		assertEquals("", p.getTrailingPunctuation());
	}

	/**
	 * Tests for onlyspacePunctuation.
	 */
	@Test
	public final void testOnlyspacePunctuation() {
		PunctuationParser p = new PunctuationParser(".,?! ");
		assertEquals(" ", p.getText());
		assertEquals(".,?!", p.getPrecedingPunctuation());
		assertEquals("", p.getTrailingPunctuation());
		p = new PunctuationParser(" .,?!");
		assertEquals(" ", p.getText());
		assertEquals("", p.getPrecedingPunctuation());
		assertEquals(".,?!", p.getTrailingPunctuation());
	}

}
