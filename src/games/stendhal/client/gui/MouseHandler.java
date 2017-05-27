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

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Locale;

/**
 * Sane mouse handling for components that want to implement drag and drop or
 * pass mouse events to EntityViews.
 */
public abstract class MouseHandler implements MouseListener, MouseMotionListener {
	/** <code>true</code> if the right mouse button is down */
	private boolean rightMouseButtonPressed;
	/** The time at which a mouse button was last pressed. */
	private long timeOfLastMousePress = System.currentTimeMillis();
	/** <code>true</code> if dragging is in progress */
	private boolean dragging;
	/**
	 * <code>true</code> if the last single click was handled; it can't be a
	 * part of a double click.
	 */
	private boolean lastClickWasHandled;
	/** Event key mask for detecting ctrl and shift clicks */
	private int flags;

	/**
	 * Called on left mouse single click.
	 *
	 * @param point location
	 * @return <code>true</code> if the click was handled
	 */
	protected abstract boolean onMouseClick(Point point);

	/**
	 * Called on left double click.
	 *
	 * @param point location
	 * @return <code>true</code> if the click was handled
	 */
	protected abstract boolean onMouseDoubleClick(Point point);

	/**
	 * Called on right mouse single click.
	 *
	 * @param point location
	 */
	protected abstract void onMouseRightClick(Point point);

	/**
	 * Called when mouse dragging starts.
	 *
	 * @param point location
	 */
	protected abstract void onDragStart(Point point);

	/**
	 * Java's official mouseClick handler; we don't use this because it doesn't
	 * register clicks while the mouse is moving at all.
	 *
	 * @param e
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButtonPressed = true;
		}

		timeOfLastMousePress = System.currentTimeMillis();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// be sure to stop dragging operations when the left button is released
		if (e.getButton() == MouseEvent.BUTTON1) {
			dragging = false;
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButtonPressed = false;
		}

		// Handle as a click if the button wasn't held for > a second
		if ((System.currentTimeMillis() - timeOfLastMousePress) < 1000) {
			onMouseClick(e);
			timeOfLastMousePress = 0;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (rightMouseButtonPressed) {
			// Disallow dragging with right button.
			return;
		}

		if (!dragging) {
			onDragStart(e.getPoint());
			dragging = true;
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// do nothing
	}

	private void onMouseClick(final MouseEvent e) {
		final Point p = e.getPoint();

		flags = e.getModifiersEx();
		// Added support for ctrl + click for Mac OS X intensifly@gmx.com
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("os x")
				&& isCtrlDown()) {
			onMouseRightClick(p);
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 1) {
				lastClickWasHandled = onMouseClick(p);
			} else if (e.getClickCount() >= 2) {
				if (lastClickWasHandled) {
					lastClickWasHandled = onMouseClick(p);
				} else {
					lastClickWasHandled = onMouseDoubleClick(p);
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// no double rightclick supported
			onMouseRightClick(p);
		}
	}

	/**
	 * Check if the control key was down during the mouse event.
	 *
	 * @return <code>true</code> if the control key was down
	 */
	protected boolean isCtrlDown() {
		return ((flags & InputEvent.CTRL_DOWN_MASK) != 0);
	}

	/**
	 * Check if the shift key was down during the mouse event.
	 *
	 * @return <code>true</code> if the shift key was down
	 */
	protected boolean isShiftDown() {
		return ((flags & InputEvent.SHIFT_DOWN_MASK) != 0);
	}
}
