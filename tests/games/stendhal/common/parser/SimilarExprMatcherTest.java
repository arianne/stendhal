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
 * Test the SimilarExprMatcher class.
 *
 * @author Martin Fuchs
 */
public class SimilarExprMatcherTest {

	@Test
	public final void testIsSimilarOnlyOneIsNull() {
		assertFalse(SimilarExprMatcher.isSimilar(null, "", 0.1));
		assertFalse(SimilarExprMatcher.isSimilar("", null, 0.1));
	}

	/**
	 * Tests for isSimilar.
	 */
	@Test
	public final void testIsSimilar() {
		assertEquals(true, SimilarExprMatcher.isSimilar(null, null, 0.5));
		assertEquals(true, SimilarExprMatcher.isSimilar("", "", 0.5));

		assertEquals(true, SimilarExprMatcher.isSimilar("A", "A", 0.1));
		assertEquals(true, SimilarExprMatcher.isSimilar("A", "a", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("A", "B", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("A", "AB", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("A", "BA", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("AB", "CD", 0.5));

		assertEquals(true, SimilarExprMatcher.isSimilar("hello", "hallo", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("hello", "hi", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("bus", "taxi", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("heart", "haert", 0.1));
		assertEquals(true, SimilarExprMatcher.isSimilar("heart", "haart", 0.1));

		assertEquals(true, SimilarExprMatcher.isSimilar("hello", "hallo", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("hello", "hi", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("telephone", "taxi", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("taxi", "bus", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("bus", "taxi", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("heart", "haert", 0.1));
		assertEquals(true, SimilarExprMatcher.isSimilar("heart", "haart", 0.1));
		assertEquals(true, SimilarExprMatcher.isSimilar("abcdefgh12345-", "-abcdefgh12345", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("abcdefgh-ABCDEFGHIJKLMN", "ABCDEFGHIJKLMN-abcdefgh", 0.1));
		assertEquals(false, SimilarExprMatcher.isSimilar("abcabcabcabc-123", "abc-123-abcabcabcabc", 0.1));
		assertEquals(true, SimilarExprMatcher.isSimilar("abcabcabcabc-abc", "abc-abcabcabcabc", 0.1));
	}

	/**
	 * Tests for similarMatching.
	 */
	@Test
	public final void testSimilarMatching() {
		final ExpressionMatcher matcher = new SimilarExprMatcher();

		final Expression e1 = new Expression("aBc", "VER");
		final Expression e2 = new Expression("abc", "VER");
		final Expression e3 = new Expression("ab", "VER");
		final Expression e4 = new Expression("abc", "SUB");
		final Expression e5 = new Expression("X", "SUB");
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		final Expression e6 = new Expression("hello", "VER");
		final Expression e7 = new Expression("hallo", "VER");
		final Expression e8 = new Expression("hailo", "VER");
		assertTrue(matcher.match(e6, e7));
		assertFalse(matcher.match(e6, e8));
		assertTrue(matcher.match(e7, e8));
	}

	/**
	 * Tests for sentenceMatching.
	 */
	@Test
	public final void testSentenceMatching() {
		final Sentence m1 = ConversationParser.parseAsMatcher("|SIMILAR|hello");
		assertFalse(m1.hasError());
		assertEquals("|SIMILAR|hello", m1.toString());

		assertEquals(true, ConversationParser.parse("hello").matchesFull(m1));
		assertEquals(true, ConversationParser.parse("hallo").matchesFull(m1));
		assertEquals(false, ConversationParser.parse("hailo").matchesFull(m1));
	}

}
