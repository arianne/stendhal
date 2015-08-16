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

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the NPC ConversationParser class.
 *
 * @author Martin Fuchs
 */
public class ConversationParserTest {

	@Test
	public final void testAmount() {
		Sentence sentence = ConversationParser.parse("buy 3 cookies");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(3, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());

		sentence = ConversationParser.parse("buy 30 cookies");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(30, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());

		sentence = ConversationParser.parse("buy 150 cookies");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(150, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());

		sentence = ConversationParser.parse("buy ten cookies");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(10, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());

		sentence = ConversationParser.parse("buy five ten cookies"); // five times ten
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(50, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());

		sentence = ConversationParser.parse("buy ten five cookies"); // ten plus five
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(15, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());
	}

	/**
	 * Tests for verboseAmount.
	 */
	@Test
	public final void testVerboseAmount() {
		final Sentence sentence = ConversationParser.parse("eat four cookies");
		assertFalse(sentence.hasError());

		assertEquals("eat", sentence.getVerbString());
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());
	}

	/**
	 * Tests for case.
	 */
	@Test
	public final void testCase() {
		final Sentence sentence = ConversationParser.parse("buy No Bread");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(0, sentence.getObject(0).getAmount());
		assertEquals("bread", sentence.getObjectName());
		assertEquals("buy No Bread", sentence.getOriginalText());
		assertEquals("buy No Bread", sentence.getTrimmedText());
		assertEquals("buy bread", sentence.getNormalized());

		assertEquals("buy bread", ConversationParser.normalize("buy any Bread"));
	}

	/**
	 * Tests for space handling.
	 */
	@Test
	public final void testSpaceHandling() {
		Sentence sentence = ConversationParser.parse("drop  three \tmeat");
		assertFalse(sentence.hasError());
		assertEquals("drop", sentence.getVerbString());
		assertEquals("drop/VER", sentence.getVerb().getNormalizedWithTypeString());
		assertEquals("drop", sentence.getTriggerExpression().getNormalized());
		assertEquals(3, sentence.getObject(0).getAmount());
		assertEquals("meat", sentence.getObjectName());
		assertEquals("drop  three 	meat", sentence.getOriginalText());
		assertEquals("drop three meat", sentence.getTrimmedText());
		assertEquals("drop meat", sentence.getNormalized());

		sentence = ConversationParser.parse(" sell house   ");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals("sell", sentence.getTriggerExpression().getNormalized());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("house", sentence.getObjectName());
		assertEquals("house", sentence.getObject(0).getMainWord());
		assertEquals("sell house", sentence.getNormalized());
	}

	/**
	 * Tests for itemName.
	 */
	@Test
	public final void testItemName() {
		final Sentence sentence = ConversationParser.parse("buy fresh fish");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals("fresh fish", sentence.getObjectName());
		assertEquals("fish", sentence.getObject(0).getMainWord());
	}

	/**
	 * Tests for error.
	 */
	@Test
	public final void testError() {
		Sentence sentence = ConversationParser.parse("hello world");
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("");
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("buy -10 cars");
		assertEquals(-10, sentence.getObject(0).getAmount());
		assertTrue(sentence.hasError());
		assertEquals("negative amount: -10", sentence.getErrorString());
	}

	/**
	 * Tests for isEmpty.
	 */
	@Test
	public final void testIsEmpty() {
		Sentence sentence = ConversationParser.parse("");
		assertFalse(sentence.hasError());
		assertTrue(sentence.isEmpty());

		sentence = ConversationParser.parse("hello");
		assertFalse(sentence.hasError());
		assertFalse(sentence.isEmpty());
	}

	/**
	 * Tests for prepositions.
	 */
	@Test
	public final void testPrepositions() {
		final Sentence sentence = ConversationParser.parse("put dish on table");
		assertFalse(sentence.hasError());

		assertEquals("put", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("dish", sentence.getObject(0).getNormalized());
		assertEquals("on", sentence.getPreposition(0).getNormalized());
		assertEquals("table", sentence.getObject(1).getNormalized());
	}

	/**
	 * Tests for pluralAndPrefix.
	 */
	@Test
	public final void testPluralAndPrefix() {
		Sentence sentence = ConversationParser.parse("buy seven bananas");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(7, sentence.getObject(0).getAmount());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("buy a bottle of wine");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("wine", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("buy two pairs of trousers");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(2, sentence.getObject(0).getAmount());
		assertEquals("trouser", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("sell 4 chaos boots");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("chaos boot", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("sell 10 bottles of poison");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals(10, sentence.getObject(0).getAmount());
		assertEquals("poison", sentence.getObject(0).getNormalized());
		assertEquals("sell poison", sentence.getNormalized());
		assertEquals("sell/VER poison/OBJ-FOO-FLU", sentence.toString());

		sentence = ConversationParser.parse("sell 10 bottles of mega poison");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals(10, sentence.getObject(0).getAmount());
		assertEquals("mega poison", sentence.getObject(0).getNormalized());
		assertEquals("sell mega poison", sentence.getNormalized());
		assertThat(sentence.toString(), anyOf(equalTo("sell/VER mega poison/OBJ"), equalTo("sell/VER mega poison/OBJ-FOO-FLU")));
	}

	/**
	 * Tests for please.
	 */
	@Test
	public final void testPlease() {
		Sentence sentence = ConversationParser.parse("please open chest");
		assertFalse(sentence.hasError());
		assertEquals("open", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("chest", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("please please do me a favour");
		assertFalse(sentence.hasError());
		assertEquals("you", sentence.getSubject(0).getNormalized());
		assertEquals("do", sentence.getVerbString());
		assertEquals("i", sentence.getSubject(1).getNormalized());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("favour", sentence.getObject(0).getNormalized());
	}

	/**
	 * Tests for me.
	 */
	@Test
	public final void testMe() {
		final Sentence sentence = ConversationParser.parse("me");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertNull(sentence.getVerbString());
		assertEquals(1, sentence.getSubjectCount());
	}

	/**
	 * Tests for twoSubjects.
	 */
	@Test
	public final void testTwoSubjects() {
		Sentence sentence = ConversationParser.parse("i love you");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("love", sentence.getVerbString());
		assertEquals("you", sentence.getSubject(1).getNormalized());
		assertEquals("i love you", sentence.getNormalized());

		sentence = ConversationParser.parse("give me 4 fishes");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(0, sentence.getSubjectCount());
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObject(0).getMainWord());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());
	}

	/**
	 * Tests for ice cream.
	 */
	@Test
	public final void testIceCream() {
		Sentence sentence = ConversationParser.parse("i would like to have an ice cream");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(1, sentence.getSubjectCount());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("icecream", sentence.getObject(0).getMainWord());
		assertEquals("icecream", sentence.getObjectName());
		assertEquals("i buy icecream!", sentence.getNormalized());
		assertEquals("i/SUB-PRO buy/VER-CON icecream/OBJ!", sentence.toString());

		sentence = ConversationParser.parse("would you like to have an ice cream?");
		assertFalse(sentence.hasError());
		assertEquals("you", sentence.getSubject(0).getNormalized());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(1, sentence.getSubjectCount());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("icecream", sentence.getObject(0).getMainWord());
		assertEquals("icecream", sentence.getObjectName());
		assertEquals("you buy icecream!", sentence.getNormalized());
		assertEquals("you/SUB-PRO buy/VER-CON icecream/OBJ!", sentence.toString());
	}

	/**
	 * Tests for teddy bears.
	 */
	@Test
	public final void testTeddyBear() {
		Sentence sentence = ConversationParser.parse("I love my teddy bear.");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("love", sentence.getVerbString());
		assertEquals(1, sentence.getSubjectCount());
		assertEquals(1, sentence.getObjectCount());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("teddy", sentence.getObject(0).getMainWord());
//		assertEquals("my teddy bear", sentence.getObjectName());
//		assertEquals("i love my teddy bear.", sentence.getNormalized());
//		assertEquals("i/SUB-PRO love/VER my teddy bear/OBJ.", sentence.toString());
		assertEquals("my teddy", sentence.getObjectName());
		assertEquals("i love my teddy.", sentence.getNormalized());
		assertEquals("i/SUB-PRO love/VER my teddy/OBJ.", sentence.toString());

		sentence = ConversationParser.parse("teddy bear");
		assertFalse(sentence.hasError());
		assertEquals("teddy", sentence.getTriggerExpression().getMainWord());
//		assertEquals("teddy bear", sentence.getTriggerExpression().getNormalized());
//		assertEquals("teddy bear", sentence.getNormalized());
//		assertEquals("teddy bear/OBJ", sentence.toString());
		assertEquals("teddy", sentence.getTriggerExpression().getNormalized());
		assertEquals("teddy", sentence.getNormalized());
		assertEquals("teddy/OBJ", sentence.toString());
	}

	/**
	 * Tests for punctuation.
	 */
	@Test
	public final void testPunctuation() {
		final Sentence sentence = ConversationParser.parse("give me 4 fishes, please");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(0, sentence.getSubjectCount());
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());

		//TODO mf - also handle "May I ask you to give me 4 fishes, please?"
		//TODO mf - also handle "If i may ask, could you please give me 4 fishes?"
	}

	/**
	 * Tests for getOriginalText() (and some more).
	 */
	@Test
	public final void testOriginalText() {
		// test the return values of getOriginalText() and some more ConversationParser/Sentence functions.
		Sentence sentence = ConversationParser.parse("Mary had a little lamb.");
		assertFalse(sentence.hasError());
		assertEquals("mary/SUB-NAM have/VER little lamb/OBJ-ANI.", sentence.toString());
		assertEquals(3, sentence.getExpressions().size());
		assertEquals(Sentence.SentenceType.STATEMENT, sentence.getType());
		assertEquals("have", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("little lamb", sentence.getObject(0).getNormalized());
		assertEquals("Mary had a little lamb.", sentence.getOriginalText());
		assertEquals("Mary had a little lamb.", sentence.getTrimmedText());
		assertEquals("mary have little lamb.", sentence.getNormalized());

		// test for preserving punctation
		sentence = ConversationParser.parse("Mary has a little, little lamb!");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.IMPERATIVE, sentence.getType());
		assertEquals("mary/SUB-NAM have/VER little little lamb/OBJ-ANI!", sentence.toString());
		assertEquals("Mary has a little, little lamb!", sentence.getOriginalText());
		assertEquals("Mary has a little little lamb!", sentence.getTrimmedText());
		assertEquals("mary have little little lamb!", sentence.getNormalized());

		// test for white space preserving
		// Note: Leading and trailing white space is trimmed always.
		sentence = ConversationParser.parse("  Mary  has  a  little  lamb  . ");
		assertFalse(sentence.hasError());
		assertEquals("mary/SUB-NAM have/VER little lamb/OBJ-ANI.", sentence.toString());
		assertEquals("Mary  has  a  little  lamb  .", sentence.getOriginalText());
		assertEquals("Mary has a little lamb.", sentence.getTrimmedText());
		assertEquals("mary have little lamb.", sentence.getNormalized());

		// test for preserving sentence type
		sentence = ConversationParser.parse("Has Mary a little lamb?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.QUESTION, sentence.getType());
		assertEquals("have/VER mary/SUB-NAM little lamb/OBJ-ANI?", sentence.toString());
		assertEquals("Has Mary a little lamb?", sentence.getOriginalText());
		assertEquals("Has Mary a little lamb?", sentence.getTrimmedText());
		assertEquals("have mary little lamb?", sentence.getNormalized());
	}

	/**
	 * Test grammer parsing.
	 */
	@Test
	public final void testGrammar()
	{
		// test for understanding question grammar
		Sentence sentence = ConversationParser.parse("Does Mary have a little lamb?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.QUESTION, sentence.getType());
		assertEquals("mary/SUB-NAM have/VER little lamb/OBJ-ANI?", sentence.toString());
		assertEquals("Does Mary have a little lamb?", sentence.getOriginalText());
		assertEquals("Mary have a little lamb?", sentence.getTrimmedText());
		assertEquals("mary have little lamb?", sentence.getNormalized());

		// test for understanding question grammar
		sentence = ConversationParser.parse("Doesn't Mary have a little lamb?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.QUESTION, sentence.getType());
		assertEquals("mary/SUB-NAM have not/VER-NEG little lamb/OBJ-ANI?", sentence.toString());
		assertEquals("Doesn't Mary have a little lamb?", sentence.getOriginalText());
		assertEquals("Mary have not a little lamb?", sentence.getTrimmedText());
		assertEquals("mary have not little lamb?", sentence.getNormalized());

		// test for a negative statement without punctation
		sentence = ConversationParser.parse("Mary doesn't have a little lamb");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.UNDEFINED, sentence.getType());
		assertEquals("mary/SUB-NAM have not/VER-NEG little lamb/OBJ-ANI", sentence.toString());
		assertEquals("mary have not little lamb", sentence.getNormalized());

		// test for a negative statement with punctation
		sentence = ConversationParser.parse("Mary doesn't have a little lamb.");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.SentenceType.STATEMENT, sentence.getType());
		assertEquals("mary/SUB-NAM have not/VER-NEG little lamb/OBJ-ANI.", sentence.toString());
		assertEquals("Mary doesn't have a little lamb.", sentence.getOriginalText());
		assertEquals("Mary have not a little lamb.", sentence.getTrimmedText());
		assertEquals("mary have not little lamb.", sentence.getNormalized());
	}

	/**
	 * Tests for compoundWords.
	 */
	@Test
	public final void testCompoundWords() {
		Sentence sentence = ConversationParser.parse("take golden ring");
		assertFalse(sentence.hasError());
		assertEquals("take", sentence.getVerbString());
		assertEquals("golden ring", sentence.getObjectName());
		assertEquals("take golden ring", sentence.getNormalized());
		assertEquals("take/VER golden ring/OBJ-COL", sentence.toString());

		sentence = ConversationParser.parse("take ring golden");
		assertFalse(sentence.hasError());
		assertEquals("take", sentence.getVerbString());
		assertEquals("ring", sentence.getObjectName());
		assertEquals("take ring golden", sentence.getNormalized());
		assertEquals("take/VER ring/OBJ golden/ADJ-COL", sentence.toString());

		sentence = ConversationParser.parse("take lich gold key");
		assertFalse(sentence.hasError());
		assertEquals("take", sentence.getVerbString());
		assertEquals("lich gold key", sentence.getObjectName());
		assertEquals("take lich gold key", sentence.getNormalized());
		assertEquals("take/VER lich gold key/OBJ", sentence.toString());

		sentence = ConversationParser.parse("take dungeon silver key");
		assertEquals("take/VER dungeon silver key/OBJ", sentence.toString());
		assertEquals("dungeon silver key", sentence.getObjectName());

		sentence = ConversationParser.parse("buy lion shield");
		assertEquals("buy/VER lion shield/OBJ", sentence.toString());
		assertEquals("lion shield", sentence.getObjectName());

		sentence = ConversationParser.parse("buy wedding ring");
		assertEquals("buy/VER wedding ring/OBJ", sentence.toString());
		assertEquals("wedding ring", sentence.getObjectName());

		sentence = ConversationParser.parse("buy engagement ring");
		assertEquals("buy/VER engagement ring/OBJ", sentence.toString());
		assertEquals("engagement ring", sentence.getObjectName());

		sentence = ConversationParser.parse("buy enhanced lion shield");
		assertEquals("buy/VER enhanced lion shield/OBJ", sentence.toString());
		assertEquals("enhanced lion shield", sentence.getObjectName());

		sentence = ConversationParser.parse("buy summon scroll");
		assertEquals("buy/VER summon scroll/OBJ", sentence.toString());
		assertEquals("buy", sentence.getVerbString());
		assertEquals("summon scroll", sentence.getObjectName());

		sentence = ConversationParser.parse("buy 1 summon scroll");
		assertEquals("buy/VER summon scroll/OBJ", sentence.toString());
		assertEquals("summon scroll", sentence.getObjectName());
	}

	/**
	 * Tests for smilies.
	 */
	@Test
	public final void testSmilies() {
		Sentence sentence = ConversationParser.parse(":-)");
		assertFalse(sentence.hasError());
		assertEquals("smile", sentence.getVerbString());
		assertEquals("smile", sentence.getNormalized());
		assertEquals("smile/VER", sentence.toString());

		sentence = ConversationParser.parse(":)");
		assertFalse(sentence.hasError());
		assertEquals("smile", sentence.getVerbString());
		assertEquals("smile", sentence.getNormalized());
		assertEquals("smile/VER", sentence.toString());

		sentence = ConversationParser.parse(":*");
		assertFalse(sentence.hasError());
		assertEquals("kiss", sentence.getVerbString());
		assertEquals("kiss", sentence.getNormalized());
		assertEquals("kiss/VER", sentence.toString());
	}

	/**
	 * Tests for nullPointer.
	 */
	@Test
	public final void testNullPointer() {
		final Sentence sentence = ConversationParser.parse(null);
		assertNotNull(sentence);
		assertEquals(0, sentence.getSubjectCount());
		assertNull(sentence.getSubject(0));
		assertEquals(0, sentence.getVerbCount());
		assertNull(sentence.getVerb(0));
		assertEquals(0, sentence.getObjectCount());
		assertNull(sentence.getObject(0));
		assertNull(sentence.getObjectName());
		assertFalse(sentence.hasError());

		SentenceImplementation impl = new SentenceImplementation(null, null);
		ConversationParser parser = new ConversationParser(impl);
		impl.parse(parser);
		assertNotNull(impl);
		assertEquals(0, impl.getSubjectCount());
		assertNull(impl.getSubject(0));
		assertEquals(0, impl.getVerbCount());
		assertNull(impl.getVerb(0));
		assertEquals(0, impl.getObjectCount());
		assertNull(impl.getObject(0));
		assertNull(impl.getObjectName());
		assertFalse(impl.hasError());
	}

	/**
	 * Tests for ignoring chat messages starting with underscores.
	 */
	@Test
	public final void testChatting() {
		final Sentence sentence = ConversationParser.parse("_Hi, how are you?");
		assertFalse(sentence.hasError());
		assertEquals(0, sentence.getExpressions().size());
		assertEquals("_Hi, how are you?", sentence.getOriginalText());
		assertEquals("", sentence.getTrimmedText());
		assertEquals("", sentence.getNormalized());
	}

	/**
	 * Tests for number.
	 */
	@Test
	public final void testNumber() {
		final Sentence sentence = ConversationParser.parse(Integer.valueOf(30).toString());
		assertNotNull(sentence);
		assertEquals(30, sentence.getExpressions().get(0).getAmount());

		assertEquals(0, sentence.getSubjectCount());
		assertEquals(0, sentence.getSubjectCount());
		assertEquals(0, sentence.getVerbCount());
		assertNull(sentence.getVerb(0));
		assertEquals(0, sentence.getObjectCount());
		assertNull(sentence.getObject(0));
		assertNull(sentence.getObjectName());
		assertFalse(sentence.hasError());
	}
}
