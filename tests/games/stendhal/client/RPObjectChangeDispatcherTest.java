package games.stendhal.client;

import static org.junit.Assert.*;
import games.stendhal.client.listener.RPObjectChangeListener;

import marauroa.common.game.RPObject;

import org.junit.Test;

public class RPObjectChangeDispatcherTest {

	/**
	 * Tests for dispatchModifyRemoved.
	 */
	@Test
	public void testDispatchModifyRemoved() {
		final RPObjectChangeListener listener = new RPObjectChangeListener() {

			public void onAdded(final RPObject object) {

			}

			public void onChangedAdded(final RPObject object, final RPObject changes) {

			}

			public void onChangedRemoved(final RPObject object, final RPObject changes) {

			}

			public void onRemoved(final RPObject object) {

			}

			public void onSlotAdded(final RPObject object, final String slotName, final RPObject sobject) {

			}

			public void onSlotChangedAdded(final RPObject object, final String slotName, final RPObject sobject, final RPObject schanges) {

			}

			public void onSlotChangedRemoved(final RPObject object, final String slotName, final RPObject sobject, final RPObject schanges) {

			}

			public void onSlotRemoved(final RPObject object, final String slotName, final RPObject sobject) {

			}
		};
		final RPObjectChangeDispatcher dispatcher = new RPObjectChangeDispatcher(listener, listener);
		dispatcher.dispatchModifyRemoved(null, null);
		assertTrue("make sure we have no NPE", true);
	}

}
