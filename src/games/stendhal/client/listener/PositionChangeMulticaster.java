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

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * A position change multicaster.
 */
public class PositionChangeMulticaster implements PositionChangeListener {
	/**
	 * The position change listeners.
	 */
	private final CopyOnWriteArrayList<PositionChangeListener> listeners =
		new CopyOnWriteArrayList<PositionChangeListener>();

	//
	// PositionChangeMulticaster
	//

	/**
	 * Add a position change listener.
	 *
	 * @param listener
	 *            The listener.
	 */
	public void add(final PositionChangeListener listener) {
		listeners.add(listener);
	}

	//
	// PositionChangeListener
	//

	/**
	 * Call position change event on all registered listeners.
	 *
	 * @param x
	 *            The new X coordinate (in world units).
	 * @param y
	 *            The new Y coordinate (in world units).
	 */
	@Override
	public void positionChanged(final double x, final double y) {
		for (final PositionChangeListener l : listeners) {
			l.positionChanged(x, y);
		}
	}
}
