/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import marauroa.common.game.Result;

public class LowerCaseValidatorTest {

	@Before
	public void setUp() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
	}

	/**
	 * Tests for lowerCaseValidator.
	 */
	@Test
	public final void testLowerCaseValidator() {
		LowerCaseValidator lcv = new LowerCaseValidator("A");
		assertEquals(Result.FAILED_INVALID_CHARACTER_USED, lcv.validate());
		lcv = new LowerCaseValidator("a");
		assertNull(lcv.validate());

		// Note: To successfully pass the following tests, Eclipse's file encoding
		// must be set to "UTF-8".
		// german Umlaut "Ue"
		lcv = new LowerCaseValidator("Ü");
		assertEquals(Result.FAILED_INVALID_CHARACTER_USED, lcv.validate());
		// german Umlaut "ue"
		lcv = new LowerCaseValidator("ü");
		assertNull(lcv.validate());
	}

}
