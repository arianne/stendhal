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
package games.stendhal.server.actions.query;


import static org.junit.Assert.assertFalse;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

public class InfoActionTest {

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		InfoAction info = new InfoAction();
		info.onAction(bob, null);
		assertFalse(bob.events().isEmpty());
		//assertEquals(null,bob.events().get(0));

	}
}
