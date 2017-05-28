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
 * A listener of position events.
 */
public interface PositionChangeListener {
	/**
	 * The user position changed.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	void positionChanged(double x, double y);
}
