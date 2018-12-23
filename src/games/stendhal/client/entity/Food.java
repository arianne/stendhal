/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003 - 2013 Stendhal                    *
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

import games.stendhal.common.constants.SoundLayer;
import marauroa.common.game.RPObject;

/**
 * A food entity.
 */
public abstract class Food extends AudibleEntity {
	/**
	 * Amount property.
	 */
	public static final Property PROP_AMOUNT = new Property();
	/** Amount attribute string. */
	private static final String AMOUNT = "amount";

	/**
	 * The current amount of food.
	 */
	private int amount;

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

		addSounds(SoundLayer.CREATURE_NOISE.groupName, "food", "click-1");

		if (object.has(AMOUNT)) {
			amount = object.getInt(AMOUNT);
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

		if (changes.has(AMOUNT)) {
			final int oldAmount = amount;
			amount = changes.getInt(AMOUNT);

			if (amount > oldAmount) {
				playRandomSoundFromCategory(SoundLayer.AMBIENT_SOUND.groupName, "food");
			}

			fireChange(PROP_AMOUNT);
		}
	}
}
