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
package games.stendhal.client.entity;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UserTest {

	/**
	 * Tests for user.
	 */
	@Test
	public final void testUser() {
		final User user = new User();

		assertFalse(User.isAdmin()); 
		assertFalse(user.hasSheep());
		assertFalse(user.hasPet());
	}

}
