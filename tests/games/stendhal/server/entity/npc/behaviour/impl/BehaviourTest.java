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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;

import org.junit.Test;

public class BehaviourTest {

	/**
	 * Tests for setAmount.
	 */
	@Test
	public void testSetAmount() {
		Behaviour beh = new Behaviour();
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
		assertTrue(horseBeh.parseRequest(sentence));

		Behaviour cowBeh = new Behaviour("cow");
		assertEquals(1, cowBeh.getItemNames().size());
		assertFalse(cowBeh.parseRequest(sentence));
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
		assertTrue(beh.parseRequest(sentence)); // only gold, silver -> unambiguous
		assertEquals(50, beh.getAmount());
		assertEquals("gold", beh.getChosenItemName());

		items.add("silver");
		beh = new Behaviour(items);
		assertFalse(beh.parseRequest(sentence)); // gold, silver -> ambiguous
		assertEquals(50, beh.getAmount());
	}

}
