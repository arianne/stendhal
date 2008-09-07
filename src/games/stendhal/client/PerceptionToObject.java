package games.stendhal.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.net.message.MessageS2CPerception;
/**
 * Translates received perception to objectlisteners.
 * 
 * @author astrid
 *
 */
public class PerceptionToObject implements IPerceptionListener {

	Map<RPObject.ID, ObjectChangeListener> map = Collections
			.synchronizedMap(new HashMap<RPObject.ID, ObjectChangeListener>());
	private ObjectFactory of;
	private static Logger logger = Logger.getLogger(PerceptionToObject.class);

	/**
	 * sets Objectfactory for callback 
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
		return false;
	}

	/**
	 * call deleted() on every Listener and resets this.
	 */
	public boolean onClear() {
		for (ObjectChangeListener listener : map.values()) {
			listener.deleted();
			
		}
		map.clear();
		return false;
	}

	public boolean onDeleted(final RPObject object) {
		ObjectChangeListener objectChangeListener = map.get(object.getID());
		if (objectChangeListener == null) {
			logger.error("no listener for: " + object);
		} else {
			objectChangeListener.deleted();
			map.remove(object.getID());
		}
		return false;
	}

	public void onException(final Exception exception,
			final MessageS2CPerception perception) {
		onClear();

	}

	public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
		ObjectChangeListener objectChangeListener = map.get(object.getID());
		if (objectChangeListener == null) {
			logger.error("no listener for: " + object);
		} else {
			objectChangeListener.modifiedAdded(changes);
		}

		return false;
	}

	public boolean onModifiedDeleted(final RPObject object,
			final RPObject changes) {
		ObjectChangeListener objectChangeListener = map.get(object.getID());
		if (objectChangeListener == null) {
			logger.error("no listener for: " + object);
		} else {
			objectChangeListener.modifiedDeleted(changes);
		}
		return false;
	}

	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		
		
		if (added != null) {
			ObjectChangeListener objectChangeListener = map.get(added.getID());
			if (objectChangeListener == null) {
				logger.error("no listener for: " + added);
			} else {
				objectChangeListener.modifiedAdded(added);
			}	
		}
		if (deleted != null) {
			ObjectChangeListener objectChangeListener = map.get(deleted.getID());
			if (objectChangeListener == null) {
				logger.error("no listener for: " + added);
			} else {
				objectChangeListener.modifiedDeleted(deleted);
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
		map.put(object.getID(), listener);

	}
	
	public void unregister(final ObjectChangeListener listener) {
		List<RPObject.ID> idList = new LinkedList<ID>();
		for (Entry<ID, ObjectChangeListener> entry : map.entrySet()) {
			if (entry.getValue() == listener) {
				idList.add(entry.getKey());
			}

		}
		for (RPObject.ID id : idList) {
			map.remove(id);
		}

	}

}
