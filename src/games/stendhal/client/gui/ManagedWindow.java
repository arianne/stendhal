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
package games.stendhal.client.gui;


/**
 * A managed window.
 */
public interface ManagedWindow {

	/**
	 * Get the managed window name.
	 * @return the name
	 *
	 *
	 */
	String getName();

	/**
	 * Get X coordinate of the window.
	 *
	 * @return A value suitable for passing to <code>moveTo()</code>.
	 */
	int getX();

	/**
	 * Get Y coordinate of the window.
	 *
	 * @return A value suitable for passing to <code>moveTo()</code>.
	 */
	int getY();

	/**
	 * Determine if the window is minimized.
	 *
	 * @return <code>true</code> if the window is minimized.
	 */
	boolean isMinimized();

	/**
	 * Determine if the window is visible.
	 *
	 * @return <code>true</code> if the window is visible.
	 */
	boolean isVisible();

	/**
	 * Move to a location. This may be subject to internal representation, and
	 * should only use what was passed from <code>getX()</code> and
	 * <code>getY()</code>.
	 *
	 * @param x
	 *            The X coordinate;
	 * @param y
	 *            The Y coordinate;
	 *
	 * @return <code>true</code> if the move was allowed.
	 */
	boolean moveTo(int x, int y);

	/**
	 * Set the window as minimized.
	 *
	 * @param minimized
	 *            Whether the window should be minimized.
	 */
	void setMinimized(boolean minimized);

	/**
	 * Set the window as visible (or hidden).
	 *
	 * @param visible
	 *            Whether the window should be visible.
	 */
	void setVisible(boolean visible);
}
