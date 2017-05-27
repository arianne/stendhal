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


import org.apache.log4j.Logger;

import games.stendhal.client.listener.RPObjectChangeListener;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A dispatcher for RPObjectChangeListeners. This normalizes the tree deltas
 * into individual object deltas.
 *
 * NOTE: The order of dispatch between contained objects and when their
 * container is very specific. Children objects are given a chance to perform
 * creation/updates before their parent is notified it happened to that specific
 * child. For cases of object removal, the parent is notified first, in case the
 * child does destruction/cleanup.
 */
class RPObjectChangeDispatcher {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(RPObjectChangeDispatcher.class);

	/**
	 * The normal listener.
	 */
	private RPObjectChangeListener listener;

	/**
	 * The user object listener.
	 */
	private RPObjectChangeListener userListener;

	/**
	 * Create an RPObjectChange event dispatcher.
	 *
	 * @param listener
	 *            The normal listener.
	 * @param userListener
	 *            The user object listener.
	 */
	RPObjectChangeDispatcher(final RPObjectChangeListener listener, final RPObjectChangeListener userListener) {
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
	 *            The object.
	 */
	void dispatchAdded(final RPObject object) {
		try {
			logger.debug("Object(" + object.getID() + ") added to client");
			fireAdded(object);
		} catch (final Exception e) {
			logger.error("dispatchAdded failed, object is " + object, e);
		}
	}

	/**
	 * Dispatch object removed event.
	 *
	 * @param object
	 *            The object.
	 */
	void dispatchRemoved(final RPObject object) {
		try {
			logger.debug("Object(" + object.getID() + ") removed from client");
			fireRemoved(object);
		} catch (final Exception e) {
			logger.error(
					"dispatchRemovedonDeleted failed, object is " + object, e);
		}
	}

	/**
	 * Dispatch object added/changed attribute(s) event.
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	void dispatchModifyAdded(final RPObject object,
			final RPObject changes) {
		try {
			logger.debug("Object(" + object.getID() + ") modified in client");
			fireChangedAdded(object, changes);
			object.applyDifferences(changes, null);
		} catch (final Exception e) {
			logger.error("dispatchModifyAdded failed, object is " + object
					+ ", changes is " + changes, e);
		}

	}

	/**
	 * Dispatch object removed attribute(s) event.
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	void dispatchModifyRemoved(final RPObject object, final RPObject changes) {
		if (object != null) {
			try {
				logger.debug("Object(" + object.getID() + ") modified in client");
				logger.debug("Original(" + object + ") modified in client");

				fireChangedRemoved(object, changes);
				object.applyDifferences(null, changes);

				logger.debug("Modified(" + object + ") modified in client");
				logger.debug("Changes(" + changes + ") modified in client");
			} catch (final Exception e) {
				logger.error("dispatchModifyRemoved failed, object is " + object + ", changes is " + changes, e);
			}
		} else {
			logger.error("dispatchModifyRemoved failed, object is null, changes is " + changes);
		}

	}

	/**
	 * Notify listeners that an object was added.
	 *
	 * @param object
	 *            The object.
	 */
	private void fireAdded(final RPObject object) {

		/*
		 * Call before children have been notified
		 */
		listener.onAdded(object);

		userListener.onAdded(object);

		/*
		 * Walk each slot
		 */
		for (final RPSlot slot : object.slots()) {
			final String slotName = slot.getName();

			for (final RPObject sobject : slot) {
				fireAdded(object, slotName, sobject);
			}
		}
	}

	/**
	 * Notify listeners that a slot object was added.
	 *
	 * @param object
	 *            The parent object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	private void fireAdded(final RPObject object, final String slotName,
			final RPObject sobject) {
		/*
		 * Notify child
		 */
		fireAdded(sobject);

		/*
		 * Call after the child has been notified
		 */
		listener.onSlotAdded(object, slotName, sobject);

		userListener.onSlotAdded(object, slotName, sobject);
	}

	/**
	 * Notify listeners that an object added/changed attribute(s). This will
	 * cascade down slot trees.
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	private void fireChangedAdded(final RPObject object, final RPObject changes) {

		/*
		 * Walk each slot
		 */
		for (final RPSlot cslot : changes.slots()) {
			if (cslot.size() != 0) {
				fireChangedAdded(object, cslot);
			}
		}

		/*
		 * Call after children have been notified
		 */
		listener.onChangedAdded(object, changes);

		userListener.onChangedAdded(object, changes);
	}

	/**
	 * Notify listeners that an object slot added/changed attribute(s). This
	 * will cascade down object trees.
	 *
	 * @param object
	 *            The base object.
	 * @param cslot
	 *            The changes slot.
	 */
	private void fireChangedAdded(final RPObject object, final RPSlot cslot) {
		final String slotName = cslot.getName();
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
		for (final RPObject schanges : cslot) {
			final RPObject.ID id = schanges.getID();

			if ((slot != null) && slot.has(id)) {
				final RPObject sobject = slot.get(id);

				listener.onSlotChangedAdded(object, slotName, sobject, schanges);

				userListener.onSlotChangedAdded(object, slotName, sobject,
						schanges);

				fireChangedAdded(sobject, schanges);
			} else {
				if (!schanges.isContained()) {
					logger.warn("!!! Not contained! - " + schanges);
				}

				fireAdded(object, slotName, schanges);
			}
		}
	}

	/**
	 * Notify listeners that an object removed attribute(s). This will cascade
	 * down slot trees.
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	private void fireChangedRemoved(final RPObject object, final RPObject changes) {

		/*
		 * Call before children have been notified
		 */
		listener.onChangedRemoved(object, changes);

		userListener.onChangedRemoved(object, changes);

		/*
		 * Walk each slot
		 */
		for (final RPSlot cslot : changes.slots()) {
			if (cslot.size() != 0) {
				fireChangedRemoved(object, cslot);
			}
		}
	}

	/**
	 * Notify listeners that an object slot removed attribute(s). This will
	 * cascade down object trees.
	 *
	 * @param object
	 *            The base object.
	 * @param cslot
	 *            The changes slot.
	 */
	private void fireChangedRemoved(final RPObject object, final RPSlot cslot) {
		final String slotName = cslot.getName();

		/*
		 * Find the original slot entry
		 */
		final RPSlot slot = object.getSlot(slotName);

		/*
		 * Walk the changes
		 */
		for (final RPObject schanges : cslot) {
			final RPObject sobject = slot.get(schanges.getID());

			if (sobject == null) {
				// This happens when a child object deleted itself
				logger.debug("Unable to find existing: " + schanges);
				continue;
			}

			/*
			 * Remove attrs vs. object [see applyDifferences()]
			 */
			if (schanges.size() > 1) {
				listener.onSlotChangedRemoved(object, slotName, sobject,
						schanges);

				userListener.onSlotChangedRemoved(object, slotName,
						sobject, schanges);

				fireChangedRemoved(sobject, schanges);
			} else {
				fireRemoved(object, slotName, sobject);
			}
		}
	}

	/**
	 * Notify listeners that an object was removed.
	 *
	 * @param object
	 *            The object.
	 */
	private void fireRemoved(final RPObject object) {
		/*
		 * Walk each slot
		 */
		for (final RPSlot slot : object.slots()) {
			final String slotName = slot.getName();

			for (final RPObject sobject : slot) {
				fireRemoved(object, slotName, sobject);
			}
		}

		/*
		 * Call after children have been notified
		 */
		listener.onRemoved(object);

		userListener.onRemoved(object);
	}

	/**
	 * Notify listeners that a slot object was removed.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	private void fireRemoved(final RPObject object, final String slotName,
			final RPObject sobject) {
		/*
		 * Call before the child is notified
		 */
		listener.onSlotRemoved(object, slotName, sobject);

		userListener.onSlotRemoved(object, slotName, sobject);

		/*
		 * Notify child
		 */
		fireRemoved(sobject);
	}


}
