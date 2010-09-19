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
package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.PortalTestHelper;

public class OneWayPortalDestinationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PortalTestHelper.generateRPClasses();
	}

	/**
	 * Tests for setDestination.
	 */
	@Test (expected = IllegalArgumentException.class)
	public final void testSetDestination() {
		final OneWayPortalDestination owp = new OneWayPortalDestination();
		owp.setDestination("bla", new Object());

	}

	/**
	 * Tests for loaded.
	 */
	@Test
	public final void testLoaded() {
		assertTrue(new OneWayPortalDestination().loaded());
	}

	/**
	 * Tests for onUsed.
	 */
	@Test
	public final void testOnUsed() {
		assertFalse(new OneWayPortalDestination().onUsed(null));
	}


}
