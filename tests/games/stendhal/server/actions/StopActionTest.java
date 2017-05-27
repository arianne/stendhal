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
package games.stendhal.server.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.attack.StopAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import utilities.PlayerTestHelper;

public class StopActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tests for onAction.
	 */
	@Test
	public void testOnAction() {

		final StopAction sa = new StopAction();
		PlayerTestHelper.generatePlayerRPClasses();
		final Player player = new Player(new RPObject()) {
			@Override
			public void stopAttack() {
				stopattack = true;

			}

			@Override
			public void notifyWorldAboutChanges() {
				notify = true;

			}

		};
		final RPAction action = new RPAction();
		sa.onAction(player, action);

		assertTrue(notify);
		assertFalse(stopattack);
		action.put("attack", "value");
		notify = false;
		stopattack = false;

		sa.onAction(player, action);

		assertTrue(notify);
		assertTrue(stopattack);

	}

	private boolean stopattack;
	private boolean notify;

}
