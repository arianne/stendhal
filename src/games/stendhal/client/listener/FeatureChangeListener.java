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

/**
 * A listener of feature changes.
 */
public interface FeatureChangeListener {
	/**
	 * A feature was disabled.
	 *
	 * @param name
	 *            The name of the feature.
	 */
	void featureDisabled(String name);

	/**
	 * A feature was enabled.
	 *
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            Optional feature specific data.
	 */
	void featureEnabled(String name, String value);
}
