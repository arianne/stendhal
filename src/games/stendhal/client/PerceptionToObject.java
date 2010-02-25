package games.stendhal.client;

import java.util.Collections;
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
public class PerceptionToObject implements IPerceptionListener {

	Map<RPObject.ID, Set<ObjectChangeListener>> map = Collections
			.synchronizedMap(new HashMap<RPObject.ID,  Set<ObjectChangeListener>>());
	private ObjectFactory of;

	/**
	 * sets Objectfactory for callback .
	 * @param of
	 */
	public void setObjectFactory(final ObjectFactory of) {
		this.of = of;
	}
	
	/**
	 * issues callback to Objectfactory.onAdded().
	 */
	public boolean onAdded(final RPObject object) {
		of.onAdded(object, this);
		this.onModifiedAdded(object, object);
		return false;
	}

	/**
	 * call deleted() on every Listener and resets this.
	 */
	public boolean onClear() {
		for (Set<ObjectChangeListener>listenerset : map.values()) {
			for (ObjectChangeListener listener : listenerset) {
				listener.deleted();
			}
		}
		map.clear();
		return false;
	}

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

	public void onException(final Exception exception,
			final MessageS2CPerception perception) {
		onClear();

	}

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

	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		
		
		if (added != null) {
			Set<ObjectChangeListener> set = map.get(added.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {
					objectChangeListener.modifiedAdded(added);
				}
			}
		}
		
		if (deleted != null) {
			Set<ObjectChangeListener> set = map.get(deleted.getID());
			if (set != null) {
				for (ObjectChangeListener objectChangeListener : set) {
					objectChangeListener.modifiedDeleted(deleted);
				}
			}
		}
		return false;
	}

	public void onPerceptionBegin(final byte type, final int timestamp) {

	}

	public void onPerceptionEnd(final byte type, final int timestamp) {

	}

	public void onSynced() {

	}

	public void onUnsynced() {

	}

	public void register(final RPObject object, final ObjectChangeListener listener) {
		if (object != null) {
		if (!map.containsKey(object.getID())) {
			map.put(object.getID(), new HashSet<ObjectChangeListener>());		
		} 
		map.get(object.getID()).add(listener);
		}
	}
	
	public void unregister(final ObjectChangeListener listener) {
		for (final Set<ObjectChangeListener> set : map.values()) {
			set.remove(listener);
		}
	}

}
