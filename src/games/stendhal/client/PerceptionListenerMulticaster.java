/*
 * @(#) src/games/stendhal/client/PerceptionListenerMulticaster.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

/**
 * A multicaster for IPerceptionListener. This will cascade perception events to
 * multiple listeners.
 */
public class PerceptionListenerMulticaster implements IPerceptionListener {
	/**
	 * Array of listeners (atomically replaced on update).
	 */
	protected IPerceptionListener[] listeners;

	/**
	 * Create a PerceptionListener multicaster.
	 */
	public PerceptionListenerMulticaster() {
		listeners = new IPerceptionListener[0];
	}

	//
	// PerceptionListenerMulticaster
	//

	/**
	 * Add a listener. This does not check for duplicates.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(IPerceptionListener listener) {
		IPerceptionListener[] newListeners;

		newListeners = new IPerceptionListener[listeners.length + 1];

		System.arraycopy(listeners, 0, newListeners, 0, listeners.length);

		newListeners[listeners.length] = listener;

		listeners = newListeners;
	}

	/**
	 * Remove a listener. This does not check for duplicates.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(IPerceptionListener listener) {
		IPerceptionListener[] newListeners;
		int idx;

		idx = listeners.length;

		while (idx-- != 0) {
			if (listeners[idx] == listener) {
				newListeners = new IPerceptionListener[listeners.length - 1];

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
	// IPerceptionListener
	//

	/**
	 * onAdded is called when an object is added to the world for first time or
	 * after a sync perception.
	 * 
	 * @param object
	 *            the added object.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onAdded(RPObject object) {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onAdded(object)) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onClear is called when the whole world is going to be cleared. It happens
	 * on sync perceptions Return true to stop further processing.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onClear() {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onClear()) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onDeleted is called when an object is removed of the world. Return true
	 * to stop further processing.
	 * 
	 * @param object
	 *            the original object.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onDeleted(RPObject object) {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onDeleted(object)) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onException is called when an exception happens.
	 * 
	 * @param e
	 *            the exception that happened.
	 * @param perception
	 *            the message that causes the problem.
	 */
	public void onException(Exception e, MessageS2CPerception perception) {
		IPerceptionListener[] listenersTemp;
		int idx;

		listenersTemp = this.listeners;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			listenersTemp[idx].onException(e, perception);
		}
	}

	/**
	 * onModifiedAdded is called when an object is modified by adding or
	 * changing one of its attributes. Return true to stop further processing.
	 * 
	 * Note that the method is called *before* modifing the object.
	 * 
	 * @param object
	 *            the original object.
	 * @param changes
	 *            the added and modified changes.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onModifiedAdded(RPObject object, RPObject changes) {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onModifiedAdded(object, changes)) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onModifiedDeleted is called each time the object has one of its
	 * attributes removed. Return true to stop further processing. Note that the
	 * method is called *before* modifing the object.
	 * 
	 * @param object
	 *            the original object.
	 * @param changes
	 *            the deleted attributes.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onModifiedDeleted(RPObject object, RPObject changes) {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onModifiedDeleted(object, changes)) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onMyRPObject is called when our rpobject avatar is processed. Return true
	 * to stop further processing.
	 * 
	 * @param added
	 *            the added and modified attributes and slots.
	 * @param deleted
	 *            the deleted attributes.
	 * 
	 * @return true to stop further processing.
	 */
	public boolean onMyRPObject(RPObject added, RPObject deleted) {
		IPerceptionListener[] listenersTemp;
		boolean stop;
		int idx;

		listenersTemp = this.listeners;
		stop = false;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			if (listenersTemp[idx].onMyRPObject(added, deleted)) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * onPerceptionBegin is called when the perception is going to be applied.
	 * 
	 * @param type
	 *            type of the perception: SYNC or DELTA.
	 * @param timestamp
	 *            the timestamp of the perception.
	 */
	public void onPerceptionBegin(byte type, int timestamp) {
		IPerceptionListener[] listenersTemp;
		int idx;

		listenersTemp = this.listeners;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			listenersTemp[idx].onPerceptionBegin(type, timestamp);
		}
	}

	/**
	 * onPerceptionBegin is called when the perception has been applied.
	 * 
	 * @param type
	 *            type of the perception: SYNC or DELTA.
	 * @param timestamp
	 *            the timestamp of the perception.
	 */
	public void onPerceptionEnd(byte type, int timestamp) {
		IPerceptionListener[] listenersTemp;
		int idx;

		listenersTemp = this.listeners;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			listenersTemp[idx].onPerceptionEnd(type, timestamp);
		}
	}

	/**
	 * onSynced is called when the client recover sync.
	 */
	public void onSynced() {
		IPerceptionListener[] listenersTemp;
		int idx;

		listenersTemp = this.listeners;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			listenersTemp[idx].onSynced();
		}
	}

	/**
	 * onUnsynced is called when the client losses sync.
	 */
	public void onUnsynced() {
		IPerceptionListener[] listenersTemp;
		int idx;

		listenersTemp = this.listeners;

		idx = listenersTemp.length;

		while (idx-- != 0) {
			listenersTemp[idx].onUnsynced();
		}
	}
}
