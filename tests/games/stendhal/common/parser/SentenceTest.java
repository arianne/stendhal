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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Test the NPC conversation Sentence class.
 *
 * @author Martin Fuchs
 */
public class SentenceTest {

	/**
	 * Grammar tests.
	 */
	@Test
	public final void testGrammar() {
		final ConversationContext ctx = new ConversationContext();

		SentenceImplementation sentence = new SentenceImplementation(ctx, "The quick brown fox jumps over the lazy dog.");
		ConversationParser parser = new ConversationParser(sentence);
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("quick/ADJ brown/ADJ-COL fox/SUB-ANI jump/VER over/PRE lazy/ADJ dog/SUB-ANI.",
				sentence.toString());

		sentence.mergeWords();
		assertEquals("quick brown fox/SUB-ANI-COL jump/VER over/PRE lazy dog/SUB-ANI.", sentence.toString());
		assertEquals(Sentence.SentenceType.STATEMENT, sentence.getType());

		sentence = new SentenceImplementation(ctx, "does it fit");
		parser = new ConversationParser(sentence);
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("do/VER it/OBJ-PRO fit/VER", sentence.toString());
		assertEquals(Sentence.SentenceType.QUESTION, sentence.evaluateSentenceType());
		assertEquals("it/OBJ-PRO fit/VER?", sentence.toString());
	}

	/**
	 * Tests for sentenceType.
	 */
	@Test
	public final void testSentenceType() {
		Sentence sentence = ConversationParser.parse("buy banana!");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.IMPERATIVE, sentence.getType());
		assertEquals("buy", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());
		assertEquals("buy banana!", sentence.getNormalized());

		sentence = ConversationParser.parse("do you have a banana for me?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.IMPERATIVE, sentence.getType());
		assertEquals("buy", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());
		assertEquals("buy banana!", sentence.getNormalized());

		sentence = ConversationParser.parse("how are you?");
		assertFalse(sentence.hasError());
		assertEquals("is/VER-PLU-QUE you/SUB-PRO?", sentence.toString());
		assertEquals(Sentence.SentenceType.QUESTION, sentence.getType());

		sentence = ConversationParser.parse("this is a banana.");
		assertFalse(sentence.hasError());
		assertEquals("this/OBJ-PRO is/VER banana/OBJ-FOO.", sentence.toString());
		assertEquals(Sentence.SentenceType.STATEMENT, sentence.getType());
		assertEquals("this", sentence.getObject(0).getNormalized());
		assertEquals("is", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(1).getNormalized());
	}

	/**
	 * Tests for enumerations.
	 */
	@Test
	public final void testEnumerations() {
		final Sentence sentence = ConversationParser.parse("it is raining cats and dogs");
		assertFalse(sentence.hasError());
		assertEquals("rain/VER-GER cat/SUB-ANI-PLU, dog/SUB-ANI-PLU.", sentence.toString());
		assertEquals(Sentence.SentenceType.STATEMENT, sentence.getType());
		assertEquals("rain", sentence.getVerb(0).getNormalized());
		assertEquals("cat", sentence.getSubject(0).getNormalized());
		assertEquals("dog", sentence.getSubject(1).getNormalized());
	}

	/**
	 * Tests for comparison.
	 */
	@Test
	public final void testComparison() {
		final Sentence s1 = ConversationParser.parse("it is raining cats and dogs");
		final Sentence s2 = ConversationParser.parse("it is raining cats, dogs");
		final Sentence s3 = ConversationParser.parse("it is raining cats but no dogs");
		assertFalse(s1.hasError());
		assertFalse(s2.hasError());
		assertFalse(s3.hasError());

		assertTrue(s1.equalsNormalized(s1));
		assertTrue(s1.equalsNormalized(s2));
		assertFalse(s1.equalsNormalized(s3));
	}

	/**
	 * Tests for matching.
	 */
	@Test
	public final void testMatching() {
		Sentence s1 = ConversationParser.parse("buy banana");
		assertFalse(s1.hasError());
		Sentence m1 = ConversationParser.parseAsMatcher("buy OBJ");
		Sentence m2 = ConversationParser.parseAsMatcher("buy SUB");
		assertFalse(m1.hasError());
		assertFalse(m2.hasError());
		assertTrue(s1.matchesFull(m1));
		assertFalse(s1.matchesFull(m2));

		s1 = ConversationParser.parse("bake apple pie");
		assertFalse(s1.hasError());
		m1 = ConversationParser.parseAsMatcher("VER *pie");
		m2 = ConversationParser.parseAsMatcher("VER *cookie");
		assertFalse(m1.hasError());
		assertFalse(m2.hasError());
		assertTrue(s1.matchesFull(m1));
		assertFalse(s1.matchesFull(m2));

		s1 = ConversationParser.parse("please work");
		m1 = ConversationParser.parseAsMatcher("IGN VER");
		m2 = ConversationParser.parseAsMatcher("VER");
		assertFalse(s1.hasError() || m1.hasError() || m2.hasError());
		assertTrue(s1.matchesFull(m1));
		assertTrue(s1.matchesFull(m2));

		s1 = ConversationParser.parse("so i love you");
		m1 = ConversationParser.parseAsMatcher("i love you");
		assertFalse(s1.hasError() || m1.hasError());
		assertTrue(s1.matchesFull(m1));

		s1 = ConversationParser.parse("but do you love me?");
		m1 = ConversationParser.parseAsMatcher("do you love me?");
		assertFalse(s1.hasError() || m1.hasError());
		assertTrue(s1.matchesFull(m1));

		s1 = ConversationParser.parse("do you know Stendhal?");
		assertEquals("you know stendhal?", s1.getNormalized());
		m1 = ConversationParser.parseAsMatcher("SUB-PRO VER Stendhal?");
		assertEquals("* * stendhal?", m1.getNormalized());
		m2 = ConversationParser.parseAsMatcher("SUB ADJ Stendhal?");
		assertEquals("* * stendhal?", m2.getNormalized());
		assertFalse(s1.hasError() || m1.hasError() || m2.hasError());
		assertTrue(s1.matchesFull(m1));
		assertFalse(s1.matchesFull(m2));

		s1 = ConversationParser.parseAsMatcher("it is raining cats and dogs");
		m1 = ConversationParser.parseAsMatcher("it is raining cats");
		assertFalse(s1.hasError());
		assertFalse(m1.hasError());
		assertFalse(s1.matchesFull(m1));
		assertTrue(s1.matchesStart(m1));
	}

	/**
	 * Tests for numberMatching.
	 */
	@Test
	public final void testNumberMatching() {
		Sentence s = ConversationParser.parse("zero");
		assertFalse(s.hasError());
		assertTrue(s.matchesNormalized("zero"));
		assertTrue(s.matchesNormalized("0"));

		s = ConversationParser.parse("no");
		assertFalse(s.hasError());
		assertTrue(s.matchesNormalized("no"));
		assertTrue(s.matchesNormalized("0"));

		s = ConversationParser.parse("one");
		assertFalse(s.hasError());
		assertTrue(s.matchesNormalized("one"));
		assertTrue(s.matchesNormalized("1"));

		s = ConversationParser.parse("two");
		assertFalse(s.hasError());
		assertTrue(s.matchesNormalized("two"));
		assertTrue(s.matchesNormalized("2"));
	}

	/**
	 * Tests for typeMatching.
	 */
	@Test
	public final void testTypeMatching() {
		Sentence s1 = ConversationParser.parse("no");
		// "0/NUM" is the normalized form of "no" because of the 0/no ambiguity.
		Sentence m1 = ConversationParser.parseAsMatcher("|TYPE|0/NUM");
		assertFalse(s1.hasError());
		assertFalse(m1.hasError());
		assertEquals("0/NUM", s1.toString());
		assertEquals("|TYPE|0/NUM", m1.toString());
		assertTrue(s1.matchesFull(m1));

		s1 = ConversationParser.parse("No");
		m1 = ConversationParser.parseAsMatcher("|TYPE|0/NUM");
		assertFalse(s1.hasError());
		assertFalse(m1.hasError());
		assertEquals("0/NUM", s1.toString());
		assertEquals("|TYPE|0/NUM", m1.toString());
		assertTrue(s1.matchesFull(m1));
	}

	/**
	 * Tests for Sentence.parse().
	 */
	@Test
	public final void testParse() {
		Sentence s1 = ConversationParser.parse("Give me a red ball please.");
		Sentence m1 = s1.parse(new ConversationContext());
		assertFalse(s1.hasError());
		assertFalse(m1.hasError());
		assertEquals("buy/VER red ball/OBJ-COL!", s1.toString());
		assertEquals("buy/VER red ball/OBJ-COL!", m1.toString());
		assertTrue(s1.matchesFull(m1));
	}

	/**
	 * Tests for Sentence.parse().
	 */
	@Test
	public final void testParseAsMatchingSource() {
		Sentence s1 = ConversationParser.parse("I am simple.");
		Sentence m1 = s1.parseAsMatchingSource();
		assertFalse(s1.hasError());
		assertFalse(m1.hasError());
		assertEquals("i/SUB-PRO am/ simple/ADJ.", s1.toString());
		assertEquals("i/SUB-PRO am/ simple/ADJ.", m1.toString());
		assertTrue(s1.matchesFull(m1));
	}

	/**
	 * Tests for diff.
	 */
	@Test
	public final void testDiff() {
		final Sentence s1 = ConversationParser.parse("It is raining cats and dogs.");
		Sentence s2 = ConversationParser.parse("it is raining cats, dogs");
		Sentence s3 = ConversationParser.parse("it is raining cats but no dogs");
		assertFalse(s1.hasError());
		assertFalse(s2.hasError());
		assertFalse(s3.hasError());

		assertEquals("", s1.diffNormalized(s1));
		assertEquals("", s1.diffNormalized(s2));
		assertEquals("-[cat] +[cat dog] -[dog]", s1.diffNormalized(s3));

		s2 = ConversationParser.parse("Let's catch a bus.");
		assertFalse(s2.hasError());
		assertEquals("catch bus.", s2.getNormalized());
		assertEquals("-[rain] +[catch] -[cat] +[bus] -[dog]", s1.diffNormalized(s2));

		s3 = ConversationParser.parse("Let's drive by bike.");
		assertFalse(s3.hasError());
		assertEquals("drive by bike.", s3.getNormalized());
		assertEquals("-[catch] +[drive] -[bus] +[by] +[bike]", s2.diffNormalized(s3));
	}

	/**
	 * Tests for specialCases.
	 */
	@Test
	public final void testSpecialCases() {
		Sentence sentence = ConversationParser.parse("where to");
		assertFalse(sentence.hasError());
		assertEquals("where/QUE?", sentence.toString());

		sentence = ConversationParser.parse("they have very good pizza");
		assertFalse(sentence.hasError());
		assertEquals("they/SUB-PRO-PLU have/VER good pizza/OBJ-FOO", sentence.toString());
	}

	/**
	 * Tests for withoutParser.
	 */
	@Test
	public final void testWithoutParser() {
		final Expression expr = new Expression("hello", "VER");
		final Sentence sentence = new SentenceImplementation(expr);

		assertTrue(sentence.matchesFull(sentence));
	}

	/**
	 * Test for the answer "pestle and mortar" in response to
	 * "Wonderful! Did you bring anything else with you?" (Ortiv Milquetoast).
	 */
	@Test
	public final void testPestleMortar() {
		Sentence sentence = ConversationParser.parse("pestle and mortar");
		assertFalse(sentence.hasError());

		assertEquals("pestle/OBJ, mortar/OBJ", sentence.toString());
		assertEquals(Sentence.SentenceType.UNDEFINED, sentence.getType());
		assertEquals("pestle", sentence.getObject(0).getNormalized());
		assertEquals("mortar", sentence.getObject(1).getNormalized());

		assertEquals("pestle and mortar", sentence.getTriggerExpression().getNormalized());
	}

	/**
	 * Test for findMatchingName().
	 */
	@Test
	public final void testFindMatchingName() {
		Sentence sentence = ConversationParser.parse("i own two pigs, a cow and a horse.");
		assertFalse(sentence.hasError());
		assertEquals("i/SUB-PRO own/VER pig/OBJ-ANI-PLU, cow/OBJ-ANI, horse/SUB-ANI.", sentence.toString());

		Set<String> names = new HashSet<String>();
		names.add("pestle");
		names.add("mortar");
		names.add("cow");
		names.add("horse");
		NameSearch found = sentence.findMatchingName(names);
		assertTrue(found.found());
		assertEquals(1, found.getAmount());
		assertEquals("horse", found.getName());
	}

	/**
	 * Test for findMatchingName() with plurals.
	 */
	@Test
	public final void testFindMatchingNamePlural() {
		Sentence sentence = ConversationParser.parse("i own two pigs, a cow and a horse.");
		assertFalse(sentence.hasError());
		assertEquals("i/SUB-PRO own/VER pig/OBJ-ANI-PLU, cow/OBJ-ANI, horse/SUB-ANI.", sentence.toString());

		Set<String> names = new HashSet<String>();
		names.add("horses");
		NameSearch found = sentence.findMatchingName(names);
		assertTrue(found.found());
		assertEquals(1, found.getAmount());
		assertEquals("horses", found.getName());

		names = new HashSet<String>();
		names.add("pig");
		found = sentence.findMatchingName(names);
		assertTrue(found.found());
		assertEquals(2, found.getAmount());
		assertEquals("pig", found.getName());

		names = new HashSet<String>();
		names.add("pigs");
		found = sentence.findMatchingName(names);
		assertTrue(found.found());
		assertEquals(2, found.getAmount());
		assertEquals("pigs", found.getName());
	}
}
