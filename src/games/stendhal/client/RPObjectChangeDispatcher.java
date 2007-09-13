/*
 * @(#) xxxxx/RPObjectChangeDispatcher.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import games.stendhal.client.events.RPObjectChangeListener;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A dispatcher for RPObjectChangeListeners.
 * This normalizes the tree deltas into individual object deltas.
 */
public class RPObjectChangeDispatcher {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(RPObjectChangeDispatcher.class);

	/**
	 * The normal listener.
	 */
	protected RPObjectChangeListener	listener;

	/**
	 * The user object listener.
	 */
	protected RPObjectChangeListener	userListener;


	/**
	 * Create an RPObjectChange event dispatcher.
	 *
	 * @param	listener	The normal listener.
	 * @param	userListener	The user object listener.
	 */
	public RPObjectChangeDispatcher(final RPObjectChangeListener listener, final RPObjectChangeListener userListener) {
		this.listener = listener;
		this.userListener = userListener;
	}


	//
	// RPObjectChangeDispatcher
	//

	/**
	 * Dispatch object added event.
	 * 
	 * @param object
	 *		The object.
	 * @param user
	 *		If this is the private user object.
	 */
	public void dispatchAdded(RPObject object, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") added to client");
			fixContainers(object);
			fireAdded(object, user);
		} catch (Exception e) {
			logger.error("dispatchAdded failed, object is " + object, e);
		}
	}

	/**
	 * Dispatch object removed event.
	 * 
	 * @param object
	 *		The object.
	 * @param user
	 *		If this is the private user object.
	 */
	public void dispatchRemoved(RPObject object, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") removed from client");
			fixContainers(object);
			fireRemoved(object, user);
		} catch (Exception e) {
			logger.error("dispatchRemovedonDeleted failed, object is " + object, e);
		}
	}

	/**
	 * Dispatch object added/changed attribute(s) event.
	 * 
	 * @param object
	 *		The base object.
	 * @param changes
	 *		The changes.
	 * @param user
	 *		If this is the private user object.
	 */
	public void dispatchModifyAdded(RPObject object, RPObject changes, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") modified in client");
			fixContainers(object);
			fixContainers(changes);
			fireChangedAdded(object, changes, user);
			object.applyDifferences(changes, null);
		} catch (Exception e) {
			logger.debug("dispatchModifyAdded failed, object is " + object + ", changes is " + changes, e);
		}

	}

	/**
	 * Dispatch object removed attribute(s) event.
	 * 
	 * @param object
	 *		The base object.
	 * @param changes
	 *		The changes.
	 * @param user
	 *		If this is the private user object.
	 */
	public void dispatchModifyRemoved(RPObject object, RPObject changes, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") modified in client");
			logger.debug("Original(" + object + ") modified in client");

			fixContainers(object);
			fixContainers(changes);
			fireChangedRemoved(object, changes, user);
			object.applyDifferences(null, changes);

			logger.debug("Modified(" + object + ") modified in client");
			logger.debug("Changes(" + changes + ") modified in client");
		} catch (Exception e) {
			logger.error("dispatchModifyRemoved failed, object is " + object + ", changes is " + changes, e);
		}
	}

	/**
	 * Dump an object out in an easily readable format.
	 * TEMP!! TEST METHOD - USED FOR DEBUGING.
	 *
	 * Probably should be in a common util class if useful long term.
	 */
	public static void dumpObject(RPObject object) {
		System.err.println(object.getRPClass().getName() + "["
				+ object.getID().getObjectID() + "]");

		for (String name : object) {
			System.err.println("  " + name + ": " + object.get(name));
		}

		System.err.println();
	}

	/**
	 * Fix parent <-> child linkage.
	 * TODO: Remove once containers are set right on creation.
	 */
	protected void fixContainers(final RPObject object) {
		for (RPSlot slot : object.slots()) {
			for (RPObject sobject : slot) {
				if (!sobject.isContained()) {
					logger.debug("Fixing container: " + slot);
					sobject.setContainer(object, slot);
				}

				fixContainers(sobject);
			}
		}
	}

	/**
	 * Notify listeners that an object was added.
	 * 
	 * @param object
	 *		The object.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireAdded(RPObject object, boolean user) {
		// TEST CODE:
		//System.err.println("fireAdded()");
		//dumpObject(object);

		listener.onAdded(object);

		// NEW CODE:
		/*
		 * Walk each slot
		 */
		for(RPSlot slot : object.slots()) {
			for(RPObject sobject : slot) {
				fireAdded(sobject, user);
			}
		}
	}

	/**
	 * Notify listeners that an object was removed.
	 * 
	 * @param object
	 *		The object.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireRemoved(RPObject object, boolean user) {
		// TEST CODE:
		//System.err.println("fireRemoved()");
		//dumpObject(object);

		// NEW CODE:
		/*
		 * Walk each slot
		 */
		for(RPSlot slot : object.slots()) {
			for(RPObject sobject : slot) {
				fireRemoved(sobject, user);
			}
		}

		listener.onRemoved(object);
	}

	/**
	 * Notify listeners that an object added/changed attribute(s).
	 * This will cascade down slot trees.
	 * 
	 * @param object
	 *		The base object.
	 * @param changes
	 *		The changes.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireChangedAdded(RPObject object, RPObject changes, boolean user) {
		// TEST CODE:
		//System.err.println("fireChangedAdded()");
		//dumpObject(changes);

		listener.onChangedAdded(object, changes);

		if (user) {
			userListener.onChangedAdded(object, changes);
		}

		/*
		 * Walk each slot
		 */
		for (RPSlot cslot : changes.slots()) {
			if (cslot.size() != 0) {
				fireChangedAdded(object, cslot, user);
			}
		}
	}

	/**
	 * Notify listeners that an object slot added/changed attribute(s).
	 * This will cascade down object trees.
	 * 
	 * @param object
	 *		The base object.
	 * @param cslot
	 *		The changes slot.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireChangedAdded(RPObject object, RPSlot cslot, boolean user) {
		String slotName = cslot.getName();
		RPSlot slot;

		/*
		 * Find the original slot entry (if any)
		 */
		if (object.hasSlot(slotName)) {
			slot = object.getSlot(slotName);
		} else {
			slot = null;
		}

		/*
		 * Walk the changes
		 */
		for (RPObject schanges : cslot) {
			RPObject.ID id = object.getID();

			if ((slot != null) && slot.has(id)) {
				RPObject sobject = slot.get(id);

				listener.onChangedAdded(object, slotName, sobject, schanges);

				if (user) {
					userListener.onChangedAdded(object, slotName, sobject, schanges);
				}

				fireChangedAdded(sobject, schanges, user);
			} else {
				if (!schanges.isContained()) {
					logger.warn("!!! Not contained! - " + schanges);
				}

				// NEW CODE:
				fireAdded(schanges, user);
			}
		}
	}

	/**
	 * Notify listeners that an object removed attribute(s).
	 * This will cascade down slot trees.
	 * 
	 * @param object
	 *		The base object.
	 * @param changes
	 *		The changes.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireChangedRemoved(RPObject object, RPObject changes, boolean user) {
		// TEST CODE:
		//System.err.println("fireChangedRemoved()");
		//dumpObject(changes);

		listener.onChangedRemoved(object, changes);

		if (user) {
			userListener.onChangedRemoved(object, changes);
		}

		/*
		 * Walk each slot
		 */
		for (RPSlot cslot : changes.slots()) {
			if (cslot.size() != 0) {
				fireChangedRemoved(object, cslot, user);
			}
		}
	}

	/**
	 * Notify listeners that an object slot removed attribute(s). This will
	 * cascade down object trees.
	 * 
	 * @param object
	 *		The base object.
	 * @param cslot
	 *		The changes slot.
	 * @param user
	 *		If this is the private user object.
	 */
	protected void fireChangedRemoved(RPObject object, RPSlot cslot, boolean user) {
		String slotName = cslot.getName();

		/*
		 * Find the original slot entry
		 */
		RPSlot slot = object.getSlot(slotName);

		/*
		 * Walk the changes
		 */
		for (RPObject schanges : cslot) {
			RPObject sobject = slot.get(schanges.getID());

			/*
			 * Remove attrs vs. object [see applyDifferences()]
			 */
			if (schanges.size() > 1) {
				listener.onChangedRemoved(object, slotName, sobject, schanges);

				if (user) {
					userListener.onChangedRemoved(object, slotName, sobject, schanges);
				}

				fireChangedRemoved(sobject, schanges, user);
			} else {
				// NEW CODE:
				fireRemoved(sobject, user);
			}
		}
	}
}
