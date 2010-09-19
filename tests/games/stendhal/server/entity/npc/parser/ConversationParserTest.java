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
package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
//		assertEquals("bread", sentence.getItemName());
		assertEquals("buy No Bread", sentence.getOriginalText());
		assertEquals("buy No Bread", sentence.getTrimmedText());
		assertEquals("buy bread", sentence.getNormalized());
	}

	/**
	 * Tests for spaceHandling.
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
//		assertEquals("house", sentence.getItemName());
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
		assertEquals("sell/VER poison/OBJ-FLU", sentence.toString());

		sentence = ConversationParser.parse("sell 10 bottles of mega poison");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals(10, sentence.getObject(0).getAmount());
		assertEquals("mega poison", sentence.getObject(0).getNormalized());
		assertEquals("sell mega poison", sentence.getNormalized());
		assertEquals("sell/VER mega poison/OBJ-FLU", sentence.toString());
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
		assertNull(sentence.getSubject(1));
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
		assertNull(sentence.getSubject(1));
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());

		sentence = ConversationParser.parse("i would like to have an ice cream");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("ice cream", sentence.getObjectName());
		assertEquals("i buy ice cream!", sentence.getNormalized());
		assertEquals("i/SUB-PRO buy/VER-CON ice cream/OBJ!", sentence.toString());

		sentence = ConversationParser.parse("would you like to have an ice cream?");
		assertFalse(sentence.hasError());
		assertEquals("you", sentence.getSubject(0).getNormalized());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("ice cream", sentence.getObjectName());
		assertEquals("you buy ice cream!", sentence.getNormalized());
		assertEquals("you/SUB-PRO buy/VER-CON ice cream/OBJ!", sentence.toString());
	}

	/**
	 * Tests for punctuation.
	 */
	@Test
	public final void testPunctuation() {
		final Sentence sentence = ConversationParser.parse("give me 4 fishes, please");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());

		//TODO mf - also handle "May I ask you to give me 4 fishes, please?"
		//TODO mf - also handle "If i may ask, could you please give me 4 fishes?"
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
		assertNull(sentence.getSubject(0));
		assertEquals(0, sentence.getVerbCount());
		assertNull(sentence.getVerb(0));
		assertEquals(0, sentence.getObjectCount());
		assertNull(sentence.getObject(0));
		assertNull(sentence.getObjectName());
		assertFalse(sentence.hasError());
	}
}
