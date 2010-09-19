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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ReservedSubStringValidatorTest {

	/**
	 * Tests for validateAdmin.
	 */
	@Test
	public final void testValidateAdmin() {
		ReservedSubStringValidator rssv = new ReservedSubStringValidator("tadmin");
		assertNotNull(rssv.validate());
		rssv = new ReservedSubStringValidator("admint");
		assertNotNull(rssv.validate());
		rssv = new ReservedSubStringValidator("admin");
		assertNotNull(rssv.validate());
		rssv = new ReservedSubStringValidator("admi");
		assertNull(rssv.validate());
	}


	/**
	 * Tests for validateGm.
	 */
	@Test
	public final void testValidateGm() {
		ReservedSubStringValidator rssv = new ReservedSubStringValidator("gm");
		assertNotNull(rssv.validate());

		rssv = new ReservedSubStringValidator("tgm");
		assertNull(rssv.validate());
		rssv = new ReservedSubStringValidator("egmond");
		assertNull(rssv.validate());
	}
}
