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
 * Test the NPC conversation parser Expression class.
 *
 * @author Martin Fuchs
 */
public class ExpressionTest {

	@Test
	public final void testAmount() {
		Sentence sentence = ConversationParser.parse("buy 15 bananas");
		assertFalse(sentence.hasError());
		Expression verb = sentence.getVerb();
		assertEquals("buy", verb.getNormalized());
		Expression object = sentence.getObject(0);
		assertEquals(15, object.getAmount());
		assertEquals(15, object.getAmountLong());

		sentence = ConversationParser.parse("sell banana");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		object = sentence.getObject(0);
		assertEquals(1, object.getAmount());
		assertEquals(1, object.getAmountLong());
	}

	@Test
	public final void testTypes() {
		Sentence sentence = ConversationParser.parse("sally, please buy 5 bananas");
		assertFalse(sentence.hasError());

		Expression verb = sentence.getVerb();
		assertEquals("buy", verb.getNormalized());
		assertTrue(verb.isVerb());
		assertFalse(verb.isObject());
		assertFalse(verb.isSubject());

		Expression subject = sentence.getSubject(0);
		assertTrue(subject.isSubject());
		assertFalse(subject.isVerb());
		Expression object = sentence.getObject(0);
		assertTrue(object.isObject());
	}

	@Test
	public final void testMatch() {
		final Expression expr1 = ConversationParser.createTriggerExpression("cloak");
		final Expression expr2 = ConversationParser.createTriggerExpression("cloaks");
		final Expression expr3 = ConversationParser.createTriggerExpression("trousers");

		assertEquals("cloak", expr1.toString());
		assertEquals("|TYPE|cloak", expr2.toString());
		assertEquals("|TYPE|trouser", expr3.toString());

		assertTrue(expr1.matches(expr1));
		assertFalse(expr1.matches(expr2));
		assertFalse(expr1.matches(expr3));

		assertFalse(expr1.matchesNormalized(expr2));
		assertTrue(expr2.matchesNormalized(expr1));
		assertFalse(expr1.matchesNormalized(expr3));
		assertFalse(expr3.matchesNormalized(expr1));

		assertFalse(expr1.matchesNormalizedSimilar(expr2));
		assertTrue(expr2.matchesNormalizedSimilar(expr1));
		assertFalse(expr1.matchesNormalizedSimilar(expr3));
		assertFalse(expr3.matchesNormalizedSimilar(expr1));
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public final void testEquals() {
		final Expression exp = new Expression("blabla");

		// compare with the same object
		assertTrue(exp.equals(exp));

		// check equals() with null parameter
		assertFalse(exp.equals(null));

		// negative equal() tests
		assertFalse(exp.equals("blabla"));

		Object x = "abc";
		Object y = new Expression("abc");
		assertFalse(y.equals(x));
		assertFalse(x.equals(y));

		assertFalse("should not break equals contract", "blabla".equals(exp));

		// positive equals() test
		x = new Expression("abc");
		y = new Expression("abc");
		assertTrue(y.equals(x));
		assertTrue(x.equals(y));
	}

	/**
	 * Tests for triggerMatching.
	 */
	@Test
	public final void testTriggerMatching() {
		final Sentence s1 = ConversationParser.parse("spade");
		final Expression e1 = s1.getTriggerExpression();
		final Sentence s2 = ConversationParser.parse("a spade");
		final Expression e2 = s2.getTriggerExpression();
		assertFalse(s1.hasError());
		assertFalse(s2.hasError());
		assertTrue(e1.matchesNormalized(e2));
		assertTrue(e2.matchesNormalized(e1));
	}

	/**
	 * Tests for typeTriggerMatching.
	 */
	@Test
	public final void testTypeTriggerMatching() {
		// First show, that "do" without the exactMatching flag matches "done".
		Sentence m1 = ConversationParser.parseAsMatcher("done");
		assertFalse(m1.hasError());
		assertEquals("do/VER-PAS", m1.toString());
		Expression e1 = m1.getTriggerExpression();

		Sentence s = ConversationParser.parse("do");
		assertFalse(s.hasError());
		Expression e2 = s.getTriggerExpression();
		assertTrue(e2.matchesNormalized(e1));
		assertEquals("do/VER", s.toString());

		// Using the typeMatching flag, it doesn't match any more...
		m1 = ConversationParser.parseAsMatcher("|TYPE|done/VER-PAS");
		assertFalse(m1.hasError());
		assertEquals("|TYPE|done/VER-PAS", m1.toString());
		e1 = m1.getTriggerExpression();

		assertFalse(e2.matches(e1));
		assertFalse(e2.matchesNormalized(e1));

		// ...but "done" matches the given type string pattern.
		s = ConversationParser.parse("done");
		assertFalse(s.hasError());
		assertEquals("do/VER-PAS", s.toString());
		e2 = s.getTriggerExpression();
		assertTrue(e2.matches(e1));
		assertTrue(e2.matchesNormalized(e1));
	}

	/**
	 * Tests for exactTriggerMatching.
	 */
	@Test
	public final void testExactTriggerMatching() {
		// First show, that "do" without the exactMatching flag matches "done".
		Sentence m1 = ConversationParser.parseAsMatcher("done");
		assertFalse(m1.hasError());
		assertEquals("do/VER-PAS", m1.toString());
		Expression e1 = m1.getTriggerExpression();

		Sentence s = ConversationParser.parse("do");
		assertFalse(s.hasError());
		Expression e2 = s.getTriggerExpression();
		assertTrue(e2.matchesNormalized(e1));
		assertEquals("do/VER", s.toString());

		// Using the exactMatching flag, it doesn't match any more...
		m1 = ConversationParser.parseAsMatcher("|EXACT|dONe");
		assertFalse(m1.hasError());
		assertEquals("|EXACT|dONe", m1.toString());
		e1 = m1.getTriggerExpression();

		assertFalse(e2.matches(e1));
		assertFalse(e2.matchesNormalized(e1));

		// ...but "done" matches the given exact matching pattern.
		s = ConversationParser.parse("dONe");
		assertFalse(s.hasError());
		assertEquals("do/VER-PAS", s.toString());
		e2 = s.getTriggerExpression();
		assertTrue(e2.matches(e1));
		assertTrue(e2.matchesNormalized(e1));
	}

}
