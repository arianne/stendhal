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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.junit.Test;

import marauroa.common.Pair;

/**
 * Tests for the string formatter.
 */
public class StringFormatTest {
	private final StringFormatter<Map<TextAttribute, Object>, TextAttributeSet> f =
			new StringFormatter<Map<TextAttribute, Object>, TextAttributeSet>();
	private final TextAttributeSet normal = new TextAttributeSet();
	private final TextAttributeSet blue;
	private final TextAttributeSet underline;

	/**
	 * Prepare the test parser.
	 */
	public StringFormatTest() {
		// The usual client definitions. Could be something else
		blue = new TextAttributeSet();
		blue.setAttribute(TextAttribute.FOREGROUND, Color.blue);
		blue.setAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		f.addStyle('#', blue);

		underline = new TextAttributeSet();
		underline.setAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		f.addStyle('§', underline);
	}

	/**
	 * Format a string.
	 *
	 * @param s string with markup
	 * @return AttributedStringBuilder filled with data from the markup string
	 */
	private DebugAttributedStringBuilder format(String s) {
		DebugAttributedStringBuilder dest = new DebugAttributedStringBuilder();
		f.format(s, normal, dest);

		return dest;
	}

	/**
	 * Check formatting text with no formatting.
	 */
	@Test
	public void testPlain() {
		DebugAttributedStringBuilder dest = format("test");
		assertEquals("test", dest.toString());
		dest.checkNext("test", normal);
		dest.assertEnd();

		dest = format("test string with no markup");
		assertEquals("test string with no markup", dest.toString());
		dest.checkNext("test string with no markup", normal);
		dest.assertEnd();

		dest = format("with a\nnewline");
		assertEquals("with a\nnewline", dest.toString());
		dest.checkNext("with a\nnewline", normal);
		dest.assertEnd();
	}

	/**
	 * Test with a single markup character type, no area markings and no
	 * quoting.
	 */
	@Test
	public void testSimpleMarkup() {
		DebugAttributedStringBuilder dest = format("#test");
		assertEquals("test", dest.toString());
		dest.checkNext("test", blue);
		dest.assertEnd();

		dest = format("#test");
		assertEquals("test", dest.toString());
		dest.checkNext("test", blue);
		dest.assertEnd();

		dest = format("##test");
		assertEquals("#test", dest.toString());
		dest.checkNext("#test", blue);
		dest.assertEnd();

		dest = format("#test#too");
		assertEquals("test#too", dest.toString());
		dest.checkNext("test#too", blue);
		dest.assertEnd();

		dest = format("#test #too");
		assertEquals("test too", dest.toString());
		dest.checkNext("test", blue);
		dest.checkNext(" ", normal);
		dest.checkNext("too", blue);
		dest.assertEnd();

		dest = format("mark at end#");
		assertEquals("mark at end", dest.toString());
		dest.checkNext("mark at end", normal);
		dest.assertEnd();

		dest = format("mark #at middle");
		assertEquals("mark at middle", dest.toString());
		dest.checkNext("mark ", normal);
		dest.checkNext("at", blue);
		dest.checkNext(" middle", normal);
		dest.assertEnd();

		dest = format("#");
		assertEquals("", dest.toString());
		dest.assertEnd();

		dest = format("##");
		assertEquals("#", dest.toString());
		dest.checkNext("#", blue);
		dest.assertEnd();
	}

	/**
	 * Test with two markup character types, no area markings and no
	 * quoting.
	 */
	@Test
	public void testSimpleCombined() {
		DebugAttributedStringBuilder dest = format("#te§st");
		assertEquals("test", dest.toString());
		dest.checkNext("te", blue);
		dest.checkNext("st", blue.union(underline));
		dest.assertEnd();

		dest = format("##test§");
		assertEquals("#test", dest.toString());
		dest.checkNext("#test", blue);
		dest.assertEnd();

		dest = format("#§test#too");
		assertEquals("testtoo", dest.toString());
		dest.checkNext("testtoo", blue.union(underline));
		dest.assertEnd();

		dest = format("#test §too");
		assertEquals("test too", dest.toString());
		dest.checkNext("test", blue);
		dest.checkNext(" ", normal);
		dest.checkNext("too", underline);
		dest.assertEnd();
	}

	/**
	 * Test markup with quotes.
	 */
	@Test
	public void testAreaMarking() {
		DebugAttributedStringBuilder dest = format("#'test'");
		assertEquals("test", dest.toString());
		dest.checkNext("test", blue);
		dest.assertEnd();

		dest = format("#'#test");
		assertEquals("#test", dest.toString());
		dest.checkNext("#test", blue);
		dest.assertEnd();

		dest = format("#'test too'");
		assertEquals("test too", dest.toString());
		dest.checkNext("test too", blue);
		dest.assertEnd();

		dest = format("#'test #too'");
		assertEquals("test #too", dest.toString());
		dest.checkNext("test #too", blue);
		dest.assertEnd();

		dest = format("mark at end#'");
		assertEquals("mark at end", dest.toString());
		dest.checkNext("mark at end", normal);
		dest.assertEnd();

		dest = format("#'");
		assertEquals("", dest.toString());
		dest.assertEnd();
	}

	/**
	 * Test nested markup.
	 */
	@Test
	public void testNesting() {
		DebugAttributedStringBuilder dest = format("#'test §'");
		assertEquals("test ", dest.toString());
		dest.checkNext("test ", blue);
		dest.assertEnd();

		dest = format("#'test §too'");
		assertEquals("test too", dest.toString());
		dest.checkNext("test ", blue);
		dest.checkNext("too", blue.union(underline));
		dest.assertEnd();

		dest = format("#'test §too' 2");
		assertEquals("test too 2", dest.toString());
		dest.checkNext("test ", blue);
		dest.checkNext("too", blue.union(underline));
		dest.checkNext(" 2", normal);
		dest.assertEnd();

		dest = format("#'test #'too''");
		assertEquals("test #too''", dest.toString());
		dest.checkNext("test #", blue);
		dest.checkNext("too''", normal);
		dest.assertEnd();

		dest = format("#'test §'too''");
		assertEquals("test too", dest.toString());
		dest.checkNext("test ", blue);
		dest.checkNext("too", blue.union(underline));
		dest.assertEnd();

		dest = format("#'test §'too'' 3");
		assertEquals("test too 3", dest.toString());
		dest.checkNext("test ", blue);
		dest.checkNext("too", blue.union(underline));
		dest.checkNext(" 3", normal);
		dest.assertEnd();
	}

	/**
	 * Test escaping markup characters.
	 */
	@Test
	public void testEscape() {
		DebugAttributedStringBuilder dest = format("#'test \\§'");
		assertEquals("test §", dest.toString());
		dest.checkNext("test §", blue);
		dest.assertEnd();

		dest = format("\\#test");
		assertEquals("#test", dest.toString());
		dest.checkNext("#test", normal);
		dest.assertEnd();

		dest = format("#'test §too\\'");
		assertEquals("test too'", dest.toString());
		dest.checkNext("test ", blue);
		dest.checkNext("too'", blue.union(underline));
		dest.assertEnd();

		dest = format("#'test #\\'too''");
		assertEquals("test #'too'", dest.toString());
		dest.checkNext("test #'too", blue);
		dest.checkNext("'", normal);
		dest.assertEnd();

		dest = format("#'test \\\\too'");
		assertEquals("test \\too", dest.toString());
		dest.checkNext("test \\too", blue);
		dest.assertEnd();
	}

	/**
	 * A version of AttributedStringBuilder that can return check the formatted
	 * sequences one by one.
	 */
	private static class DebugAttributedStringBuilder extends AttributedStringBuilder {
		/** Store of formatted string slices. */
		private final Queue<Pair<String, TextAttributeSet>> data = new LinkedList<Pair<String, TextAttributeSet>>();

		@Override
		public void append(String s, TextAttributeSet attrs) {
			super.append(s, attrs);
			data.add(new Pair<String, TextAttributeSet>(s, attrs));
		}

		/**
		 * Check the next string element and its attributes.
		 *
		 * @param s string element
		 * @param attrs attributes
		 */
		void checkNext(String s, TextAttributeSet attrs) {
			Pair<String, TextAttributeSet> p = data.poll();
			assertNotNull(p);
			/*
			 * Attempt to combine with the next element if they have the same
			 * attributes, so that we'll be testing behavior rather than
			 * implementation details
			 */
			boolean combined = false;
			do {
				combined = false;
				Pair<String, TextAttributeSet> p2 = data.peek();
				if ((p2 != null) && p2.second().contents().equals(p.second().contents())) {
					p = new Pair<String, TextAttributeSet>(p.first() + p2.first(), p.second());
					data.remove();
					combined = true;
				}
			} while (combined);

			assertEquals("String element differs from expected", s, p.first());

			// Check that all wanted attributes are present and have the right
			// values
			for (Entry<TextAttribute, Object> e : attrs.contents().entrySet()) {
				Object value = p.second().contents().get(e.getKey());
				assertNotNull("Missing attribute " + e.getKey(), value);
				assertEquals(e.getValue(), value);
			}
			// Check that there are no unwanted values
			for (Entry<TextAttribute, Object> e : p.second().contents().entrySet()) {
				assertTrue("Unwanted key " + e.getKey() + " with value " + e.getValue(),
						attrs.contents().containsKey(e.getKey()));
			}
		}

		/**
		 * Ensure that all the encodings have been checked.
		 */
		void assertEnd() {
			assertNull("Extra data after end", data.poll());
		}
	}
}
