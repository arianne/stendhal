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
package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Test CommandlineParser.
 *
 * @author Martin Fuchs
 */
public class CommandlineParserTest {

	@Test
	public final void testGetNextParameter() {
		ErrorBuffer errors = new ErrorBuffer();
		CommandlineParser parser = new CommandlineParser("who");
		assertEquals("who", parser.getNextParameter(errors));
		assertEquals(null, parser.getNextParameter(errors));
		assertEquals(false, errors.hasError());

		errors = new ErrorBuffer();
		parser = new CommandlineParser("where ghost");
		assertEquals("where", parser.getNextParameter(errors));
		assertEquals("ghost", parser.getNextParameter(errors));
		assertEquals(null, parser.getNextParameter(errors));
		assertEquals(false, errors.hasError());
	}

	/**
	 * Tests for readAllParameters.
	 */
	@Test
	public final void testReadAllParameters() {
		ErrorBuffer errors = new ErrorBuffer();
		CommandlineParser parser = new CommandlineParser("jail player minutes reason");
		List<String> paras = parser.readAllParameters(errors);
		assertEquals("[jail, player, minutes, reason]", paras.toString());
		assertEquals(false, errors.hasError());

		errors = new ErrorBuffer();
		parser = new CommandlineParser("/hello, how are you?");
		paras = parser.readAllParameters(errors);
		assertEquals("[/hello,, how, are, you?]", paras.toString());
		assertEquals(false, errors.hasError());
	}

	/**
	 * Tests for quoting.
	 */
	@Test
	public final void testQuoting() {
		final ErrorBuffer errors = new ErrorBuffer();
		final CommandlineParser parser = new CommandlineParser("where 'player 2'");
		assertFalse(errors.hasError());
		assertEquals("where", parser.getNextParameter(errors));
		assertEquals("player 2", parser.getNextParameter(errors));
		assertEquals(null, parser.getNextParameter(errors));
	}

	/**
	 * Tests for error.
	 */
	@Test
	public final void testError() {
		final ErrorBuffer errors = new ErrorBuffer();
		final CommandlineParser parser = new CommandlineParser("TEST 'abc...");
		assertFalse(errors.hasError());
		assertEquals("TEST", parser.getNextParameter(errors));
		assertEquals("abc...", parser.getNextParameter(errors));
		assertTrue(errors.hasError());
		assertEquals(null, parser.getNextParameter(errors));
	}

}
