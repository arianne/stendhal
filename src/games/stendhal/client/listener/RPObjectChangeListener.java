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
package games.stendhal.client.listener;

import marauroa.common.game.RPObject;

/**
 * A listener of RPObject changes.
 */
public interface RPObjectChangeListener {
	/**
	 * An object was added.
	 *
	 * @param object
	 *            The object.
	 */
	void onAdded(RPObject object);

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	void onChangedAdded(RPObject object, RPObject changes);

	/**
	 * The object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	void onChangedRemoved(RPObject object, RPObject changes);

	/**
	 * An object was removed.
	 *
	 * @param object
	 *            The object.
	 */
	void onRemoved(RPObject object);

	/**
	 * A slot object was added.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	void onSlotAdded(RPObject object, String slotName, RPObject sobject);

	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	void onSlotChangedAdded(RPObject object, String slotName, RPObject sobject,
			RPObject schanges);

	/**
	 * A slot object removed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	void onSlotChangedRemoved(RPObject object, String slotName,
			RPObject sobject, RPObject schanges);

	/**
	 * A slot object was removed.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	void onSlotRemoved(RPObject object, String slotName, RPObject sobject);
}
