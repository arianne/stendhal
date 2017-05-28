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

public class Sign extends Entity {
	/**
	 * The sign text.
	 */
	private String text;

	/** the action to display on right click */
	private String action;

	//
	// Sign
	//

	/**
	 * Get the sign text.
	 *
	 * @return The sign text.
	 */
	public String getText() {
		return text;
	}


	/**
	 * gets the action
	 *
	 * @return action
	 */
	public String getAction() {
		return action;
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

		if (changes.has("action")) {
			action = changes.get("action");
		}

		if (changes.has("text")) {
			text = changes.get("text");
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

		if (changes.has("text")) {
			text = "";
		}
	}
}
