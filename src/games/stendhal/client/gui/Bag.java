/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;


import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.listener.FeatureChangeListener;

/**
 * A bag.
 */
@SuppressWarnings("serial")
class Bag extends SlotWindow implements FeatureChangeListener {

	/**
	 * Create a bag
	 */
	public Bag() {
		super("bag", 3, 4);
		setCloseable(false);
	}

	/**
	 * A feature was enabled.
	 *
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            Optional feature specific data.
	 */
	@Override
	public void featureEnabled(final String name, String value) {
		if (!name.equals("bag")) {
			return;
		}

		if (value.equals("")) {
			value = "3 4";
		}
		String[] values = value.split(" ");
		setSlotsLayout(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
		setAcceptedTypes(EntityMap.getClass("item", null, null));
	}


	@Override
	public void featureDisabled(String name) {
		setSlotsLayout(3, 4);
		setAcceptedTypes(EntityMap.getClass("item", null, null));
	}
}
