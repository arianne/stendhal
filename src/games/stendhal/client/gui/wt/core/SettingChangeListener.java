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
package games.stendhal.client.gui.wt.core;

/**
 * Interface for monitoring configuration changes.
 */
public interface SettingChangeListener {
	/**
	 * Called when the watched setting changes.
	 *
	 * @param newValue new value of the setting
	 */
	void changed(String newValue);
}
