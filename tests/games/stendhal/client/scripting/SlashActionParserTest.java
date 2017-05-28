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
package games.stendhal.client.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.common.StringHelper;
import utilities.QuestHelper;

/**
 * Test SlashActionParser class.
 *
 * @author Martin Fuchs
 */
public class SlashActionParserTest {

	@BeforeClass
	public static void setupclass() throws Exception {
		QuestHelper.setUpBeforeClass();
		SlashActionRepository.register();
	}

	/**
	 * Tests for parse.
	 */
	@Test
	public final void testParse() {
		SlashActionCommand cmd = SlashActionParser.parse("who");
		assertFalse(cmd.hasError());
		assertEquals("who", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("unknown-command");
		assertFalse(cmd.hasError());
		assertEquals("unknown-command", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals(null, cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("where ghost");
		assertFalse(cmd.hasError());
		assertEquals("where", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("ghost", cmd.getRemainder());

		cmd = SlashActionParser.parse("adminlevel player 100");
		assertFalse(cmd.hasError());
		assertEquals("adminlevel", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("player", cmd.getParams()[0]);
		assertEquals("100", cmd.getParams()[1]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("jail player minutes reason");
		assertFalse(cmd.hasError());
		assertEquals("jail", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("player", cmd.getParams()[0]);
		assertEquals("minutes", cmd.getParams()[1]);
		assertEquals("reason", cmd.getRemainder());

		cmd = SlashActionParser.parse("/hello, how are you?");
		assertFalse(cmd.hasError());
		assertEquals("/", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("hello, how are you?", cmd.getRemainder());

		cmd = SlashActionParser.parse("/ thanks, I'm fine");
		assertFalse(cmd.hasError());
		assertEquals("/", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("thanks, I'm fine", cmd.getRemainder());
	}

	/**
	 * Tests for quoting.
	 */
	@Test
	public final void testQuoting() {
		SlashActionCommand cmd = SlashActionParser.parse("where 'player 2'");
		assertFalse(cmd.hasError());
		assertEquals("where", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("player 2", StringHelper.unquote(cmd.getRemainder()));

		cmd = SlashActionParser.parse("say \"i don't speak german\"");
		assertFalse(cmd.hasError());
		assertEquals("say", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("i don't speak german", cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());
	}

	/**
	 * Tests for error.
	 */
	@Test
	public final void testError() {
		SlashActionCommand cmd = SlashActionParser.parse("");
		assertTrue(cmd.hasError());
		assertEquals("Missing slash command", cmd.getErrorString());

		cmd = SlashActionParser.parse("where 'abc");
		assertFalse(cmd.hasError());
	}

	/**
	 * Tests for ban.
	 */
	@Test
	public final void testBan() {
		SlashActionCommand cmd = SlashActionParser.parse("ban bob reason");
		assertFalse(cmd.hasError());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("bob", cmd.getParams()[0]);
		assertEquals("reason", cmd.getParams()[1]);
		assertEquals("", cmd.getRemainder());


		 cmd = SlashActionParser.parse("ban bob");
		assertTrue(cmd.hasError());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("bob", cmd.getParams()[0]);
		assertEquals(null, cmd.getParams()[1]);
		assertEquals("", cmd.getRemainder());

	}


}
