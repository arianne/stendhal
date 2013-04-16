/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for the string formatter.
 */
public class StringFormatTest {
	final StringFormatter<Map<TextAttribute, Object>, TextAttributeSet> f = 
			new StringFormatter<Map<TextAttribute,Object>, TextAttributeSet>();
	final TextAttributeSet normal = new TextAttributeSet();

	/**
	 * Prepare the test parser.
	 */
	public StringFormatTest() {
		// The usual client definitions. Could be something else
		TextAttributeSet set = new TextAttributeSet();
		set.setAttribute(TextAttribute.FOREGROUND, Color.blue);
		set.setAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		f.addStyle('#', set);
		
		set = new TextAttributeSet();
		set.setAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		f.addStyle('§', set);
	}
	
	/**
	 * Format a string.
	 * 
	 * @param s string with markup
	 * @return AttributedStringBuilder filled with data from the markup string
	 */
	private AttributedStringBuilder format(String s) {
		AttributedStringBuilder dest = new AttributedStringBuilder();
		f.format(s, normal, dest);
		
		return dest;
	}
	
	/**
	 * Check formatting text with no formatting.
	 */
	@Test
	public void testPlain() {
		AttributedStringBuilder dest = format("test");
		assertEquals("test", dest.toString());
		
		dest = format("test string with no markup");
		assertEquals("test string with no markup", dest.toString());
		
		dest = format("with a\nnewline");
		assertEquals("with a\nnewline", dest.toString());
	}
	
	/**
	 * Test with a single markup character type, no area markings and no
	 * quoting.
	 */
	@Test
	public void testSimpleMarkup() {
		AttributedStringBuilder dest = format("#test");
		assertEquals("test", dest.toString());
		
		dest = format("##test");
		assertEquals("#test", dest.toString());
		
		dest = format("#test#too");
		assertEquals("test#too", dest.toString());
		
		dest = format("#test #too");
		assertEquals("test too", dest.toString());
		
		dest = format("mark at end#");
		assertEquals("mark at end", dest.toString());
		
		dest = format("#");
		assertEquals("", dest.toString());
		
		dest = format("##");
		assertEquals("#", dest.toString());
	}
	
	/**
	 * Test with two markup character types, no area markings and no
	 * quoting.
	 */
	@Test
	public void testSimpleCombined() {
		AttributedStringBuilder dest = format("#te§st");
		assertEquals("test", dest.toString());
		
		dest = format("##test§");
		assertEquals("#test", dest.toString());
		
		dest = format("#§test#too");
		assertEquals("testtoo", dest.toString());
		
		dest = format("#test §too");
		assertEquals("test too", dest.toString());
	}
	
	/**
	 * Test markup with quotes.
	 */
	@Test
	public void testAreaMarking() {
		AttributedStringBuilder dest = format("#'test'");
		assertEquals("test", dest.toString());
		
		dest = format("#'#test");
		assertEquals("#test", dest.toString());
		
		dest = format("#'test too'");
		assertEquals("test too", dest.toString());
		
		dest = format("#'test #too'");
		assertEquals("test #too", dest.toString());
		
		dest = format("mark at end#'");
		assertEquals("mark at end", dest.toString());
		
		dest = format("#'");
		assertEquals("", dest.toString());
	}
	
	/**
	 * Test nested markup.
	 */
	@Test
	public void testNesting() {
		AttributedStringBuilder dest = format("#'test §'");
		assertEquals("test ", dest.toString());
		
		dest = format("#'test §too'");
		assertEquals("test too", dest.toString());
		
		dest = format("#'test #'too''");
		assertEquals("test #too''", dest.toString());
		
		dest = format("#'test §'too''");
		assertEquals("test too", dest.toString());
	}
	
	/**
	 * Test escaping markup characters.
	 */
	@Test
	public void testEscape() {
		AttributedStringBuilder dest = format("#'test \\§'");
		assertEquals("test §", dest.toString());
		
		dest = format("\\#test");
		assertEquals("#test", dest.toString());
		
		dest = format("#'test §too\\'");
		assertEquals("test too'", dest.toString());
		
		dest = format("#'test #\\'too''");
		assertEquals("test #'too'", dest.toString());
		
		dest = format("#'test \\\\too'");
		assertEquals("test \\too", dest.toString());
	}
}
