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
package games.stendhal.client;

import java.awt.Point;
import java.awt.geom.Point2D;

import games.stendhal.client.gui.j2d.RemovableSprite;
import games.stendhal.client.gui.j2d.entity.EntityView;

public interface IGameScreen {

	/** The width / height of one tile. */
	int SIZE_UNIT_PIXELS = 32;

	/** Prepare screen for the next frame to be rendered and move it if needed .*/
	void nextFrame();

	/**
	 * Center the view.
	 */
	 void center();

	/**
	 * Set the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	void setOffline(boolean offline);

	/**
	 * Removes a text bubble.
	 * @param entity The text to be removed.
	 */
	void removeText(RemovableSprite entity);

	/**
	 * Removes all the text entities.
	 */
	void clearTexts();

	/**
	 * Gets an entity view at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	EntityView<?> getEntityViewAt(double x, double y);

	/**
	 * Get a movable entity view at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	EntityView<?> getMovableEntityViewAt(final double x,
			final double y);

	/**
	 * Get the text bubble at specific coordinates.
	 *
	 * @param x
	 *            Screen X coordinate.
	 * @param y
	 *            Screen Y world coordinate.
	 * @return the text bubble at the given coordinate or <code>null</code> if
	 * 	not found.
	 */
	RemovableSprite getTextAt(int x, int y);

	/**
	 * Convert screen view coordinates to world coordinates.
	 *
	 * @param p
	 *            The screen view coordinates.
	 *
	 * @return World coordinates.
	 */
	Point2D convertScreenViewToWorld(final Point p);

	/**
	 * Convert screen view coordinates to world coordinates.
	 *
	 * @param x
	 *            The screen view X coordinate.
	 * @param y
	 *            The screen view Y coordinate.
	 *
	 * @return World coordinates.
	 */
	Point2D convertScreenViewToWorld(final int x, final int y);

	/**
	 * The user position changed. This sets the target coordinates that the
	 * screen centers on.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	void positionChanged(final double x, final double y);
}
