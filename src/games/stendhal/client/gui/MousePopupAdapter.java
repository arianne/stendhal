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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A mouse listener implementation for right click menu triggering to avoid
 * copying the code everywhere.
 */
public abstract class MousePopupAdapter extends MouseAdapter {
	@Override
	public void mousePressed(final MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		maybeShowPopup(e);
	}

	/**
	 * Called at potential popup triggers.
	 *
	 * @param e
	 */
	private final void maybeShowPopup(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	/**
	 * Called when the operating system dependent popup trigger has been
	 *	triggered. It is still up to the component to decide if it should
	 *	actually show a popup.
	 *
	 * @param e
	 */
	protected abstract void showPopup(MouseEvent e);
}
