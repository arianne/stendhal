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
 * A portal which can be "used" by the player. Use a Door if you want some
 * sprites for it.
 */
public class Portal extends InvisibleEntity {
	/**
	 * Whether the portal is hidden.
	 */
	private boolean hidden;

	//
	// Portal
	//

	/**
	 * Determine if the portal is hidden.
	 *
	 * @return <code>true</code> if hidden.
	 */
	public boolean isHidden() {
		return hidden;
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

		hidden = object.has("hidden");
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

		if (changes.has("hidden")) {
			hidden = true;
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

		if (changes.has("hidden")) {
			hidden = false;
		}
	}


	/**
	 * Is this entity useable?
	 *
	 * @return true if it is useable, false otherwise
	 */
	public boolean isUseable() {
		return rpObject.has("use");
	}
}
