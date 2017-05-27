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
package games.stendhal.server.core.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import marauroa.common.game.Result;

public class MaxLengthValidatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tests for maxLengthValidator.
	 */
	@Test
	public void testMaxLengthValidator() {
		MaxLengthValidator validator = new MaxLengthValidator("four", 4);
		assertNull(validator.validate());

		validator = new MaxLengthValidator("four", 5);
		assertNull(validator.validate());

		validator = new MaxLengthValidator("four", 3);
		assertEquals(Result.FAILED_STRING_TOO_LONG, validator.validate());

	}



}
