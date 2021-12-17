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
package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import marauroa.common.game.RPAction;

public class VolumeActionTest {

	@After
	public void tearDown() throws Exception {
		StendhalClient.resetClient();
	}

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals(action.get("type"), "volume");
			}
		};
		final VolumeAction action = new VolumeAction();

		String[] param = {null};
		assertTrue(action.execute(param, "remainder"));

	}

	/**
	 * Tests for change volume.
	 */
	@Test
	public void testChangeVolume() {
		final VolumeAction action = new VolumeAction();

		String[] param = {"master", "50"};
		assertTrue(action.execute(param, "remainder"));
		StendhalClient.resetClient();
		assertEquals("50", WtWindowManager.getInstance().getProperty("sound.volume.master", null));

	}

	/**
	 * Tests for getMaximumParameters().
	 */
	@Test
	public void testGetMaximumParameters() {
		final VolumeAction action = new VolumeAction();
		assertThat(action.getMaximumParameters(), is(2));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final VolumeAction action = new VolumeAction();
		assertThat(action.getMinimumParameters(), is(0));
	}

}
