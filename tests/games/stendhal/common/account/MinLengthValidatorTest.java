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

import games.stendhal.server.core.account.MinLengthValidator;


public class MinLengthValidatorTest {

	/**
	 * Tests for emptyString.
	 */
	@Test
	public void testEmptyString() {
		final MinLengthValidator validator = new MinLengthValidator("", 4);
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for shortString.
	 */
	@Test
	public void testShortString() {
		final MinLengthValidator validator = new MinLengthValidator("asd", 4);
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for oKString.
	 */
	@Test
	public void testOKString() {
		final MinLengthValidator validator = new MinLengthValidator("asdf", 4);
		Assert.assertNull(validator.validate());
	}
}
