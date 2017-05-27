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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link ScalingModel} that implements ChangeListener handling, but nothing
 * else.
 */
public abstract class AbstractScalingModel implements ScalingModel {
	private final List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();
	private int representation;

	@Override
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Set the representation, and notify listeners if it changed.
	 *
	 * @param representation new representation
	 * @return <code>true</code> if the representation changed, otherwise
	 * 	<code>false</code>
	 */
	protected boolean setRepresentation(int representation) {
		int oldRepresentation = this.representation;
		this.representation = representation;
		if (oldRepresentation != representation) {
			fireChanged();
			return true;
		}
		return false;
	}

	@Override
	public int getRepresentation() {
		return representation;
	}

	/**
	 * Notify change listeners.
	 */
	protected void fireChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : listeners) {
			listener.stateChanged(e);
		}
	}
}
