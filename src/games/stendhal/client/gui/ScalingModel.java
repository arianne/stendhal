/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import javax.swing.event.ChangeListener;

/**
 * A model interface for values that need a representation as values [0, max],
 * where the representation is not necessarily the same as the original value,
 * for example the length of a health bar.
 */
public interface ScalingModel {
	/**
	 * Add a listener that should be notified when the representation of the
	 * value changes.
	 *
	 * @param listener change listener
	 */
	void addChangeListener(ChangeListener listener);
	/**
	 * Set the internal value.
	 *
	 * @param value new value
	 */
	void setValue(double value);
	/**
	 * Get the representation value.
	 *
	 * @return representation
	 */
	int getRepresentation();
	/**
	 * Set the maximum representation value.
	 *
	 * @param max new maximum
	 */
	void setMaxRepresentation(int max);
}
