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
package utilities;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

public class PlayerTestHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAddEmptySlots() {
		PlayerTestHelper.generatePlayerRPClasses();
		final Player bob = new Player(new RPObject());
		PlayerTestHelper.addEmptySlots(bob);

		assertTrue(bob.hasSlot("bag"));
		assertTrue(bob.hasSlot("lhand"));
		assertTrue(bob.hasSlot("rhand"));
		assertTrue(bob.hasSlot("armor"));
		assertTrue(bob.hasSlot("head"));
		assertTrue(bob.hasSlot("legs"));
		assertTrue(bob.hasSlot("feet"));
		assertTrue(bob.hasSlot("finger"));
		assertTrue(bob.hasSlot("cloak"));
		assertTrue(bob.hasSlot("keyring"));
		assertTrue(bob.hasSlot("!quests"));
		assertTrue(bob.hasSlot("!kills"));
		assertTrue(bob.hasSlot("!tutorial"));
		assertTrue(bob.hasSlot("!visited"));

	}

}
