/*
 * @(#) src/games/stendhal/client/events/EntityHolderMulticaster.java
 *
 * $Id$
 */

package games.stendhal.client.events;

//
//

import games.stendhal.client.entity.Entity;

/**
 * An entity holder (slot or link) event multicaster.
 */
public class EntityHolderMulticaster implements EntityHolderListener {
	/**
	 * The entity holder listeners.
	 */
	protected EntityHolderListener[] listeners;

	/**
	 * Create a entity holder multicaster.
	 */
	public EntityHolderMulticaster() {
		listeners = new EntityHolderListener[0];
	}

	//
	// EntityHolderMulticaster
	//

	/**
	 * Add a entity holder listener.
	 * 
	 * @param l
	 *            The listener.
	 */
	public void add(EntityHolderListener l) {
		int len = listeners.length;

		EntityHolderListener[] newListeners = new EntityHolderListener[len + 1];
		System.arraycopy(listeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		listeners = newListeners;
	}

	/**
	 * Remove a entity holder listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void remove(EntityHolderListener listener) {
		int idx = listeners.length;

		while (idx-- != 0) {
			if (listeners[idx] == listener) {
				EntityHolderListener[] newListeners = new EntityHolderListener[listeners.length - 1];

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
	// EntityHolderListener
	//

	/**
	 * An entity was added.
	 * 
	 * @param parent
	 *            The parent entity (if known).
	 * @param name
	 *            The name of the holder.
	 * @param entity
	 *            The entity.
	 */
	public void entityAdded(final Entity parent, final String name,
			final Entity entity) {
		EntityHolderListener[] list = listeners;

		for (EntityHolderListener l : list) {
			l.entityAdded(parent, name, entity);
		}
	}

	/**
	 * An entity was removed.
	 * 
	 * @param parent
	 *            The parent entity (if known).
	 * @param name
	 *            The name of the holder.
	 * @param entity
	 *            The entity.
	 */
	public void entityRemoved(final Entity parent, final String name,
			final Entity entity) {
		EntityHolderListener[] list = listeners;

		for (EntityHolderListener l : list) {
			l.entityRemoved(parent, name, entity);
		}
	}
}
