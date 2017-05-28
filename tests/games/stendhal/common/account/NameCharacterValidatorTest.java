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
package games.stendhal.common.account;

import org.junit.Assert;
import org.junit.Test;

import games.stendhal.server.core.account.NameCharacterValidator;


public class NameCharacterValidatorTest {

	/**
	 * Tests for specialCharcter.
	 */
	@Test
	public void testSpecialCharcter() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf_");
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for startingWithNumber.
	 */
	@Test
	public void testStartingWithNumber() {
		final NameCharacterValidator validator = new NameCharacterValidator("1asdf");
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for oKString.
	 */
	@Test
	public void testOKString() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf");
		Assert.assertNull(validator.validate());
	}
}
