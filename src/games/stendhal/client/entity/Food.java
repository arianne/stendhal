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

import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.SoundMaster;
import marauroa.common.game.RPObject;

/**
 * A food entity.
 */
public abstract class Food extends Entity {
	/**
	 * Amount property.
	 */
	public static final Property PROP_AMOUNT = new Property();

	/**
	 * The current amount of food.
	 */
	private int amount;

	/**
	 * Create a food entity.
	 */
	public Food() {
	}

	//
	// Food
	//

	/**
	 * Get the amount.
	 * 
	 * @return The amount.
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
	 * A slot object added/changed attribute(s).
	 * 
	 * @param object
	 *            The base slot object.
	 * @param changes
	 *            The slot changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (changes.has("amount")) {
			final int oldAmount = amount;
			amount = changes.getInt("amount");

			// TODO this causes problems because of unidentified content refresh
			// events (e.g. synchronizing)
			if (amount > oldAmount) {
				SoundMaster.play(SoundLayer.CREATURE_NOISE, "pop-2.wav", getX(), getY());
			}

			fireChange(PROP_AMOUNT);
		}
	}
}
