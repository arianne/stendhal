/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import games.stendhal.client.entity.Inspector;

/**
 * Interface for objects that can be inspected.
 */
public interface Inspectable {
	/**
	 * Set the Inspector used for inspection.
	 *
	 * @param inspector inspector for the object
	 */
	void setInspector(Inspector inspector);
}
