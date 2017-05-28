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

//
//

import marauroa.common.game.RPObject;

/**
 * A blood entity.
 */
public class Blood extends Entity {
	/**
	 * Amount property.
	 */
	public static final Property PROP_AMOUNT = new Property();

	/**
	 * The amount of blood.
	 */
	private int amount;

	/**
	 * Create a blood entity.
	 */
	public Blood() {
	}

	//
	// Blood
	//

	/**
	 * Get the current amount.
	 *
	 * @return The current amount.
	 */
	public int getAmount() {
		return amount;
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
		 * Amount
		 */
		if (object.has("amount")) {
			amount = object.getInt("amount");
		} else {
			amount = 0;
		}
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
		 * Amount
		 */
		if (changes.has("amount")) {
			amount = changes.getInt("amount");
			fireChange(PROP_AMOUNT);
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
		 * Amount
		 */
		if (changes.has("amount")) {
			amount = 0;
			fireChange(PROP_AMOUNT);
		}
	}
}
