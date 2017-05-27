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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the ExpressionMatcher class.
 *
 * @author Martin Fuchs
 */
public class ExpressionMatcherTest {

	@Test
	public final void testInit() {
		final ExpressionMatcher matcher = new ExpressionMatcher();

		assertTrue(matcher.isEmpty());
		assertFalse(matcher.isAnyFlagSet());
	}

	/**
	 * Tests for parsing.
	 */
	@Test
	public final void testParsing() {
		final ExpressionMatcher matcher = new ExpressionMatcher();

		Sentence sentence = matcher.parseSentence("", new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("", sentence.toString());

		String str = matcher.readMatchingFlags("Lazy dog");
		assertEquals("Lazy dog", str);
		assertTrue(matcher.isEmpty());
		assertEquals("", matcher.toString());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("lazy dog/SUB-ANI", sentence.toString());

		str = matcher.readMatchingFlags("|TYPE|abcdef/OBJ");
		assertEquals("abcdef/OBJ", str);
		assertTrue(matcher.isAnyFlagSet());
		assertFalse(matcher.getExactMatching());
		assertTrue(matcher.getTypeMatching());
		assertEquals("|TYPE", matcher.toString());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("|TYPE|abcdef/OBJ", sentence.toString());

		str = matcher.readMatchingFlags("|EXACT|Hello world!");
		assertEquals("Hello world!", str);
		assertTrue(matcher.isAnyFlagSet());
		assertTrue(matcher.getExactMatching());
		assertFalse(matcher.getTypeMatching());
		assertEquals("|EXACT", matcher.toString());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("|EXACT|Hello world!", sentence.toString());
	}

	/**
	 * Tests for typeMatching.
	 */
	@Test
	public final void testTypeMatching() {
		final ExpressionMatcher matcher = new ExpressionMatcher();

		final Expression e1 = new Expression("abc", "VER");
		final Expression e2 = new Expression("abc", "VER");
		final Expression e3 = new Expression("ab", "VER");
		final Expression e4 = new Expression("abc", "SUB");
		final Expression e5 = new Expression("X", "SUB");

		matcher.setTypeMatching(false);
		matcher.setExactMatching(false);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(true);
		matcher.setExactMatching(false);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertFalse(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(true);
		matcher.setExactMatching(true);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertFalse(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(false);
		matcher.setExactMatching(true);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));
	}

	/**
	 * Tests for exactMatching.
	 */
	@Test
	public final void testExactMatching() {
		final ExpressionMatcher matcher = new ExactExprMatcher();

		final Expression abcVER = new Expression("abc", "VER");
		final Expression abcVERCopy = new Expression("abc", "VER");
		final Expression ab = new Expression("ab", "VER");
		final Expression abcSUB = new Expression("abc", "SUB");
		final Expression X = new Expression("X", "SUB");
		final Expression aBc = new Expression("aBc", "SUB");

		assertTrue(matcher.match(abcVER, abcVERCopy));
		assertFalse(matcher.match(abcVER, ab));
		assertTrue(matcher.match(abcVER, abcSUB));
		assertFalse(matcher.match(abcVER, X));
		assertFalse(matcher.match(abcSUB, X));
		assertFalse(matcher.match(abcVER, aBc));
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public final void testEquals() {
		final ExpressionMatcher m1 = new ExpressionMatcher();
		final ExpressionMatcher m2 = new ExpressionMatcher();
		assertEquals(m1, m2);
		assertEquals(m1, m1);

		assertFalse(m1.equals(null));

		m1.setCaseInsensitive(true);
		assertFalse(m1.equals(m2));
		assertFalse(m2.equals(m1));

		assertEquals(m1, m1);

		m2.setCaseInsensitive(true);
		assertEquals(m1, m2);
	}

}
