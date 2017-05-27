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

import org.junit.Test;

/**
 * Test the ErrorBuffer class.
 *
 * @author Martin Fuchs
 */
public class ErrorBufferTest {

	@Test
	public final void test() {
		final ErrorDrain errors = new ErrorBuffer();

		assertEquals(false, errors.hasError());

		errors.setError("error 1 occured");
		assertEquals(true, errors.hasError());
		assertEquals("error 1 occured", errors.getErrorString());

		errors.setError("error 2 occured");
		assertEquals(true, errors.hasError());
		assertEquals("error 1 occured\nerror 2 occured", errors.getErrorString());
	}

}
