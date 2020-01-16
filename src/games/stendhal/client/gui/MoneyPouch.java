/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.sprite.Sprite;

/**
 * Slot for carrying money.
 */
public class MoneyPouch extends ItemPanel implements FeatureChangeListener {

	MoneyPouch(final String slotName, final Sprite placeholder) {
		super(slotName, placeholder);
	}

	@Override
	public void featureDisabled(final String name) {
		if (name.equals("pouch")) {
			setVisible(false);
		}
	}

	@Override
	public void featureEnabled(final String name, final String value) {
		if (name.equals("pouch")) {
			setVisible(true);
		}
	}
}
