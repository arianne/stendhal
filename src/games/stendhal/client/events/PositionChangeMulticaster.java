/*
 * @(#) src/games/stendhal/client/events/PositionChangeMulticaster.java
 *
 * $Id$
 */

package games.stendhal.client.events;

//
//

/**
 * A position change multicaster.
 */
public class PositionChangeMulticaster implements PositionChangeListener {
	/**
	 * The position change listeners.
	 */
	protected PositionChangeListener[] listeners;

	/**
	 * Create a position change multicaster.
	 */
	public PositionChangeMulticaster() {
		listeners = new PositionChangeListener[0];
	}

	//
	// PositionChangeMulticaster
	//

	/**
	 * Add a position change listener.
	 * 
	 * @param l
	 *            The listener.
	 */
	public void add(PositionChangeListener l) {
		int len = listeners.length;

		PositionChangeListener[] newListeners = new PositionChangeListener[len + 1];
		System.arraycopy(listeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		listeners = newListeners;
	}

	/**
	 * Remove a position change listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void remove(PositionChangeListener listener) {
		int idx = listeners.length;

		while (idx-- != 0) {
			if (listeners[idx] == listener) {
				PositionChangeListener[] newListeners = new PositionChangeListener[listeners.length - 1];

				if (idx != 0) {
					System.arraycopy(listeners, 0, newListeners, 0, idx);
				}

				if (++idx != listeners.length) {
					System.arraycopy(listeners, idx, newListeners, idx - 1,
							listeners.length - idx);
				}

				listeners = newListeners;
				break;
			}
		}
	}

	//
	// PositionChangeListener
	//

	/**
	 * Call position change event on all registered listeners.
	 * 
	 * @param x
	 *            The new X coordinate (in world units).
	 * @param y
	 *            The new Y coordinate (in world units).
	 */
	public void positionChanged(final double x, final double y) {
		PositionChangeListener[] list = listeners;

		for (PositionChangeListener l : list) {
			l.positionChanged(x, y);
		}
	}
}
