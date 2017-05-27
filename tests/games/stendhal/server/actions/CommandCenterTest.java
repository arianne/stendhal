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
package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;

public class CommandCenterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
	}

	/**
	 * Tests for register.
	 */
	@Test
	public void testRegister() {
		final ActionListener listener = new ActionListener() {
			@Override
			public void onAction(final Player player, final RPAction action) {
				player.put("success", "true");
			}
		};
		final RPAction action = new RPAction();
		action.put("type", "action");
		final Player caster = PlayerTestHelper.createPlayer("player");
		CommandCenter.register("action", listener);
		assertFalse(caster.has("success"));
		CommandCenter.execute(caster, action);
		assertTrue(caster.has("success"));
	}

	/**
	 * Tests for registerTwice.
	 */
	@Test
	public void testRegisterTwice() {
		CommandCenter.register("this", new ActionListener() {

			@Override
			public void onAction(final Player player, final RPAction action) {
				// do nothing
			}
		});

		CommandCenter.register("this", new ActionListener() {
			@Override
			public void onAction(final Player player, final RPAction action) {
				// do nothing
			}
		});
	}

	/**
	 * Tests for executeNullNull.
	 */
	@Test
	public void testExecuteNullNull() {
		CommandCenter.execute(null, null);

	}

	/**
	 * Tests for executeUnknown.
	 */
	@Test
	public void testExecuteUnknown() {
		final RPAction action = new RPAction();

		CommandCenter.register("this", new ActionListener() {
			@Override
			public void onAction(final Player player, final RPAction action) {
				// do nothing
			}
		});
		CommandCenter.register("that", new ActionListener() {
			@Override
			public void onAction(final Player player, final RPAction action) {
				// do nothing
			}
		});
		CommandCenter.register("thus", new ActionListener() {
			@Override
			public void onAction(final Player player, final RPAction action) {
				// do nothing
			}
		});


		action.put("type", "");
		final Player caster = PlayerTestHelper.createPlayer("bob");
		CommandCenter.execute(caster, action);
		assertEquals("Unknown command /. Please type #/help to get a list.", caster.events().get(0).get("text"));
		caster.clearEvents();

		action.put("type", "taat");
		CommandCenter.execute(caster, action);
		assertEquals("Unknown command /taat. Did you mean #/that? Or type #/help to get a list.", caster.events().get(0).get("text"));
		caster.clearEvents();

		action.put("type", "thos");
		CommandCenter.execute(caster, action);
		assertEquals("Unknown command /thos. Did you mean #/this or #/thus? Or type #/help to get a list.", caster.events().get(0).get("text"));
		caster.clearEvents();

		action.put("type", "thas");
		CommandCenter.execute(caster, action);
		assertEquals("Unknown command /thas. Did you mean #/that, #/this or #/thus? Or type #/help to get a list.", caster.events().get(0).get("text"));
	}
}
