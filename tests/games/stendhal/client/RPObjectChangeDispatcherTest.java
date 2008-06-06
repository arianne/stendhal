package games.stendhal.client;

import static org.junit.Assert.*;
import games.stendhal.client.events.RPObjectChangeListener;

import marauroa.common.game.RPObject;

import org.junit.Test;

public class RPObjectChangeDispatcherTest {

	@Test
	public void testDispatchModifyRemoved() {
		RPObjectChangeListener listener = new RPObjectChangeListener() {

			public void onAdded(RPObject object) {

			}

			public void onChangedAdded(RPObject object, RPObject changes) {

			}

			public void onChangedRemoved(RPObject object, RPObject changes) {

			}

			public void onRemoved(RPObject object) {

			}

			public void onSlotAdded(RPObject object, String slotName, RPObject sobject) {

			}

			public void onSlotChangedAdded(RPObject object, String slotName, RPObject sobject, RPObject schanges) {

			}

			public void onSlotChangedRemoved(RPObject object, String slotName, RPObject sobject, RPObject schanges) {

			}

			public void onSlotRemoved(RPObject object, String slotName, RPObject sobject) {

			}
		};
		RPObjectChangeDispatcher dispatcher = new RPObjectChangeDispatcher(listener, listener);
		dispatcher.dispatchModifyRemoved(null, null, false);
		dispatcher.dispatchModifyRemoved(null, null, true);
		assertTrue("make sure we have no NPE", true);
	}

}
