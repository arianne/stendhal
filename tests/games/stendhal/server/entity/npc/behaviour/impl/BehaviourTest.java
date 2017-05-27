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
package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;

public class BehaviourTest {

	/**
	 * Tests for setAmount.
	 */
	@Test
	public void testSetAmount() {
		ItemParserResult beh = new ItemParserResult(true, "X", 1, null);
		beh.setAmount(0);
		assertEquals(1, beh.getAmount());
		beh.setAmount(1001);
		assertEquals(1, beh.getAmount());
		beh.setAmount(1000);
		assertEquals(1000, beh.getAmount());
		beh.setAmount(2);
		assertEquals(2, beh.getAmount());
	}

	/**
	 * Tests for parseRequest.
	 */
	@Test
	public void testParseRequest() {
		Sentence sentence = ConversationParser.parse("sell horse");
		assertFalse(sentence.hasError());

		Behaviour horseBeh = new Behaviour("horse");
		assertEquals(1, horseBeh.getItemNames().size());
		assertTrue(horseBeh.parse(sentence).wasFound());

		Behaviour cowBeh = new Behaviour("cow");
		assertEquals(1, cowBeh.getItemNames().size());
		assertFalse(cowBeh.parse(sentence).wasFound());
	}

	/**
	 * Tests for parseNumbers.
	 */
	@Test
	public void testParseRequestNumber() {
		Sentence sentence = ConversationParser.parse("50");
		assertFalse(sentence.hasError());

		Set<String> items = new HashSet<String>();
		items.add("gold");
		Behaviour beh = new Behaviour(items);
		ItemParserResult res = beh.parse(sentence);
		assertTrue(res.wasFound()); // only gold, silver -> unambiguous
		assertEquals(50, res.getAmount());
		assertEquals("gold", res.getChosenItemName());

		items.add("silver");
		beh = new Behaviour(items);
		res = beh.parse(sentence);
		assertFalse(res.wasFound()); // gold, silver -> ambiguous
		assertEquals(50, res.getAmount());
	}

}
