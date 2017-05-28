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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
/**
 * Translates received perception to objectlisteners.
 *
 * @author astrid
 *
 */
class PerceptionToObject implements IPerceptionListener {
	final Map<RPObject.ID, Set<ObjectChangeListener>> map = new HashMap<RPObject.ID,  Set<ObjectChangeListener>>();
	private final ObjectFactory of = new ObjectFactory();

	/**
	 * issues callback to Objectfactory.onAdded().
	 */
	@Override
	public boolean onAdded(final RPObject object) {
		of.onAdded(object, this);
		this.onModifiedAdded(object, object);
		return false;
	}

	/**
	 * call deleted() on every Listener and resets this.
	 */
	@Override
	public boolean onClear() {
		for (Set<ObjectChangeListener>listenerset : map.values()) {
			for (ObjectChangeListener listener : listenerset) {
				listener.deleted();
			}
		}
		map.clear();
		return false;
	}

	@Override
	public boolean onDeleted(final RPObject object) {
		if (object != null) {
			Set<ObjectChangeListener> set = map.get(object.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {

					objectChangeListener.deleted();
					map.remove(object.getID());
				}
			}
		}
		return false;
	}

	@Override
	public void onException(final Exception exception,
			final MessageS2CPerception perception) {
		onClear();
	}

	@Override
	public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
		if (object != null) {
			Set<ObjectChangeListener> set = map.get(object.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {

					objectChangeListener.modifiedAdded(changes);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onModifiedDeleted(final RPObject object,
			final RPObject changes) {
		if (object != null) {
			Set<ObjectChangeListener> set = map.get(object.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {
					objectChangeListener.modifiedDeleted(changes);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		if (deleted != null) {
			Set<ObjectChangeListener> set = map.get(deleted.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {
					objectChangeListener.modifiedDeleted(deleted);
				}
			}
		}

		if (added != null) {
			Set<ObjectChangeListener> set = map.get(added.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {
					objectChangeListener.modifiedAdded(added);
				}
			}
		}

		return false;
	}

	@Override
	public void onPerceptionBegin(final byte type, final int timestamp) {
	}

	@Override
	public void onPerceptionEnd(final byte type, final int timestamp) {
	}

	@Override
	public void onSynced() {
	}

	@Override
	public void onUnsynced() {
	}

	void register(final RPObject object, final ObjectChangeListener listener) {
		if (object != null) {
		if (!map.containsKey(object.getID())) {
			map.put(object.getID(), new HashSet<ObjectChangeListener>());
		}
		map.get(object.getID()).add(listener);
		}
	}

	void unregister(final ObjectChangeListener listener) {
		for (final Set<ObjectChangeListener> set : map.values()) {
			set.remove(listener);
		}
	}
}
