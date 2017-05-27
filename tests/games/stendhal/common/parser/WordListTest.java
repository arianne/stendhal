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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

/**
 * Test the NPC conversation WordList class.
 *
 * @author Martin Fuchs
 */
public class WordListTest {

	@Test
	public final void testNouns() {
		final WordList wl = WordList.getInstance();

		WordEntry w = wl.find("house");
		assertNotNull(w);
		assertTrue(w.getType().isObject());
		assertEquals(ExpressionType.OBJECT, w.getTypeString());
		assertFalse(w.isPlural());
		assertEquals("houses", w.getPlurSing());

		w = wl.find("man");
		assertNotNull(w);
		assertTrue(w.getType().isSubject());
		assertEquals(ExpressionType.SUBJECT, w.getTypeString());
		assertFalse(w.isPlural());
		assertEquals("men", w.getPlurSing());

		w = wl.find("men");
		assertNotNull(w);
		assertTrue(w.getType().isSubject());
		assertEquals(ExpressionType.SUBJECT+ExpressionType.SUFFIX+ExpressionType.PLURAL, w.getTypeString());
		assertTrue(w.isPlural());
		assertEquals("man", w.getPlurSing());

		w = wl.find("carrot");
		assertNotNull(w);
		assertTrue(w.getType().isObject());
		assertEquals(ExpressionType.OBJECT + ExpressionType.SUFFIX_FOOD, w.getTypeString());
		assertFalse(w.isPlural());
		assertEquals("carrots", w.getPlurSing());

		w = wl.find("carrots");
		assertNotNull(w);
		assertTrue(w.getType().isObject());
		assertEquals(ExpressionType.OBJECT + ExpressionType.SUFFIX_FOOD + ExpressionType.SUFFIX_PLURAL, w.getTypeString());
		assertTrue(w.isPlural());
		assertEquals("carrot", w.getPlurSing());

		w = wl.find("water");
		assertNotNull(w);
		assertTrue(w.getType().isObject());
		assertEquals(ExpressionType.OBJECT + ExpressionType.SUFFIX_FOOD + ExpressionType.SUFFIX_FLUID, w.getTypeString());
		assertEquals("waters", w.getPlurSing());

		w = wl.find("she");
		assertNotNull(w);
		assertTrue(w.getType().isSubject());
		assertEquals(ExpressionType.SUBJECT + ExpressionType.SUFFIX_PRONOUN, w.getTypeString());
		assertEquals("they", w.getPlurSing());
	}

	/**
	 * Tests for verbs.
	 */
	@Test
	public final void testVerbs() {
		final WordList wl = WordList.getInstance();

		WordEntry w = wl.find("say");
		assertNotNull(w);
		assertTrue(w.getType().isVerb());
		assertEquals(ExpressionType.VERB, w.getTypeString());

		w = wl.find("open");
		assertNotNull(w);
		assertTrue(w.getType().isVerb());
		assertEquals(ExpressionType.VERB, w.getTypeString());

		w = wl.find("are");
		assertNotNull(w);
		assertTrue(w.getType().isVerb());
		assertEquals(ExpressionType.VERB + ExpressionType.SUFFIX_PLURAL, w.getTypeString());
	}

	/**
	 * Tests for adjectives.
	 */
	@Test
	public final void testAdjectives() {
		final WordList wl = WordList.getInstance();

		WordEntry w = wl.find("white");
		assertNotNull(w);
		assertTrue(w.getType().isAdjective());
		assertEquals(ExpressionType.ADJECTIVE + ExpressionType.SUFFIX_COLOR, w.getTypeString());

		w = wl.find("silvery");
		assertNotNull(w);
		assertTrue(w.getType().isAdjective());
		assertEquals(ExpressionType.ADJECTIVE + ExpressionType.SUFFIX_COLOR, w.getTypeString());

		w = wl.find("nomadic");
		assertNotNull(w);
		assertTrue(w.getType().isAdjective());
		assertEquals(ExpressionType.ADJECTIVE, w.getTypeString());
	}

	/**
	 * Tests for prepositions.
	 */
	@Test
	public final void testPrepositions() {
		final WordList wl = WordList.getInstance();

		WordEntry w = wl.find("with");
		assertNotNull(w);
		assertTrue(w.getType().isPreposition());
		assertEquals(ExpressionType.PREPOSITION, w.getTypeString());

		w = wl.find("on");
		assertNotNull(w);
		assertTrue(w.getType().isPreposition());
		assertEquals(ExpressionType.PREPOSITION, w.getTypeString());
	}

	/**
	 * Tests for plural.
	 */
	@Test
	public final void testPlural() {
		final WordList wl = WordList.getInstance();

		assertEquals("houses", wl.plural("house"));
		assertEquals("cookies", wl.plural("cookie"));
		assertEquals("cookies", wl.plural("cooky"));
	}

	/**
	 * Tests for singular.
	 */
	@Test
	public final void testSingular() {
		final WordList wl = WordList.getInstance();

		assertEquals("house", wl.singular("houses"));
		assertEquals("cookie", wl.singular("cookies"));
	}

	/**
	 * Tests for trimWords.
	 */
	@Test
	public void testTrimWords() {
		String word = "BLABLA";
		assertThat(WordList.trimWord(word), is("blabla"));
		assertThat(word, is("BLABLA"));
		word = "";
		assertThat(WordList.trimWord(word), is(""));

		word = "\'";
		assertThat(WordList.trimWord(word), is(""));

		word = "\'\'";
		assertThat(WordList.trimWord(word), is(""));

		word = "b\'\'";
		assertThat(WordList.trimWord(word), is("b"));

		word = "\'\'B\'L\'A\'B\'L\'A\'\'";
		assertThat(WordList.trimWord(word), is("b\'l\'a\'b\'l\'a"));
	}

	/**
	 * Tests for WordEntry.print().
	 */
	@Test
	public final void testPrintWord() {
		final WordList wl = WordList.getInstance();

		assertEquals("blue\tADJ-COL", printWord(wl, "blue"));
		assertEquals("with\tPRE", printWord(wl, "with"));
		assertEquals("mary\tSUB-NAM", printWord(wl, "mary"));
		assertEquals("doesn't\tVER-NEG	=do", printWord(wl, "doesn't"));
		assertEquals("three\tNUM\t3", printWord(wl, "three"));
		assertEquals("houses\tOBJ-PLU\thouse", printWord(wl, "houses"));
	}

	private String printWord(final WordList wl, String word) {
		WordEntry w = wl.find(word);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		w.print(pw, word);

		return sw.toString();
	}

	/**
	 * Tests for compound names.
	 */
	@Test
	public final void testCompoundNames() {
		final WordList wl = WordList.getInstance();

		Sentence sentence = ConversationParser.parse("call Sherlock Holmes");
		assertFalse(sentence.hasError());
		assertEquals("call/VER sherlock/ holmes/SUB-NAM", sentence.toString());
		assertEquals(3, sentence.getExpressions().size());
		assertEquals("Holmes", sentence.getSubject(0).getOriginal());

		sentence = ConversationParser.parse("listen Elvis Presley");
		assertFalse(sentence.hasError());
		assertEquals("listen/VER elvis presley/", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertNull("There should be no recognized subject.", sentence.getSubject(0));

		wl.registerSubjectName("Sherlock Holmes");
		wl.registerSubjectName("John Watson");
		wl.registerSubjectName("Professor Moriarty");
		wl.registerSubjectName("Baby Herman");
		wl.registerSubjectName("Lady Gaga");
		wl.registerSubjectName("Elvis Presley");

		sentence = ConversationParser.parse("call Sherlock Holmes");
		assertFalse(sentence.hasError());
		assertEquals("call/VER sherlock holmes/SUB-NAM-DYN", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertEquals("Sherlock Holmes", sentence.getSubject(0).getOriginal());

		sentence = ConversationParser.parse("call John Watson");
		assertFalse(sentence.hasError());
		assertEquals("call/VER john watson/SUB-NAM-DYN", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertEquals("John Watson", sentence.getSubject(0).getOriginal());

		sentence = ConversationParser.parse("call professor moriarty");
		assertFalse(sentence.hasError());
		assertEquals("call/VER professor moriarty/SUB-NAM-DYN", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertEquals("professor moriarty", sentence.getSubject(0).getOriginal());

		sentence = ConversationParser.parse("look for baby Herman");
		assertFalse(sentence.hasError());
		assertEquals("look/VER for/PRE baby herman/SUB-NAM-DYN", sentence.toString());
		assertEquals(3, sentence.getExpressions().size());
		assertEquals("baby herman", sentence.getSubject(0).getNormalized());

		sentence = ConversationParser.parse("look for Lady Gaga");
		assertFalse(sentence.hasError());
		assertEquals("look/VER for/PRE lady gaga/SUB-NAM-DYN", sentence.toString());
		assertEquals(3, sentence.getExpressions().size());
		assertEquals("lady gaga", sentence.getSubject(0).getNormalized());

		sentence = ConversationParser.parse("listen Elvis Presley");
		assertFalse(sentence.hasError());
		assertEquals("listen/VER elvis presley/SUB-NAM-DYN", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertEquals("elvis presley", sentence.getSubject(0).getNormalized());

		wl.unregisterSubjectName("Sherlock Holmes");
		wl.unregisterSubjectName("John Watson");
		wl.unregisterSubjectName("Professor Moriarty");
		wl.unregisterSubjectName("Baby Herman");
		wl.unregisterSubjectName("Lady Gaga");
		wl.unregisterSubjectName("Elvis Presley");
	}

	/**
	 * Tests for compound names containing non-SUBject expressions.
	 */
	@Test
	public final void testComplexCompoundNames() {
		final WordList wl = WordList.getInstance();

		Sentence sentence = ConversationParser.parse("catch Bugs Bunny");
		assertFalse(sentence.hasError());
		assertEquals("catch/VER bug bunny/OBJ-ANI", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertNull("There should be no recognized subject.", sentence.getSubject(0));

		wl.registerSubjectName("Bugs Bunny");

		sentence = ConversationParser.parse("catch Bugs Bunny");
		assertFalse(sentence.hasError());
		assertEquals("catch/VER bugs bunny/SUB-NAM", sentence.toString());
		assertEquals(2, sentence.getExpressions().size());
		assertEquals("Bugs Bunny", sentence.getSubject(0).getOriginal());
		assertEquals("bugs bunny", sentence.getSubject(0).getNormalized());

		wl.unregisterSubjectName("Bugs Bunny");
	}
}
