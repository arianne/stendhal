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
package games.stendhal.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.client.listener.RPObjectChangeListener;
import marauroa.common.game.RPObject;

public class RPObjectChangeDispatcherTest {

	/**
	 * Tests for dispatchModifyRemoved.
	 */
	@Test
	public void testDispatchModifyRemoved() {
		final RPObjectChangeListener listener = new RPObjectChangeListener() {

			@Override
			public void onAdded(final RPObject object) {

			}

			@Override
			public void onChangedAdded(final RPObject object, final RPObject changes) {

			}

			@Override
			public void onChangedRemoved(final RPObject object, final RPObject changes) {

			}

			@Override
			public void onRemoved(final RPObject object) {

			}

			@Override
			public void onSlotAdded(final RPObject object, final String slotName, final RPObject sobject) {

			}

			@Override
			public void onSlotChangedAdded(final RPObject object, final String slotName, final RPObject sobject, final RPObject schanges) {

			}

			@Override
			public void onSlotChangedRemoved(final RPObject object, final String slotName, final RPObject sobject, final RPObject schanges) {

			}

			@Override
			public void onSlotRemoved(final RPObject object, final String slotName, final RPObject sobject) {

			}
		};
		final RPObjectChangeDispatcher dispatcher = new RPObjectChangeDispatcher(listener, listener);
		dispatcher.dispatchModifyRemoved(null, null);
		assertTrue("make sure we have no NPE", true);
	}

}
