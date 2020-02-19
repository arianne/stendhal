/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import marauroa.common.game.RPObject;

/**
 * A door entity.
 */
public class Door extends Portal {
	/**
	 * Open state property.
	 */
	public static final Property PROP_OPEN = new Property();

	/**
	 * Whether the door is open.
	 */
	private boolean open;

	/**
	 * Create a door entity.
	 */
	public Door() {
		// default constructor
	}

	//
	// Door
	//

	/**
	 * Check if the door is open.
	 *
	 * @return <code>true</code> if the door is open.
	 */
	public boolean isOpen() {
		return open;
	}

	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 *
	 * @param object
	 *            The object.
	 *
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		/*
		 * Open state
		 */
		open = object.has("open");
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Open state
		 */
		if (changes.has("open")) {
			open = true;
			fireChange(PROP_OPEN);
		}
	}

	/**
	 * The object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		/*
		 * Open state
		 */
		if (changes.has("open")) {
			open = false;
			fireChange(PROP_OPEN);
		}
	}
}
