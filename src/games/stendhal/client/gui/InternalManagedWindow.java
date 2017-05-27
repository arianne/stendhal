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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import games.stendhal.client.gui.styled.cursor.CursorRepository;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * An InternalWindow that implements ManagedWindow. Intended for the various
 * on screen windows.
 */
public class InternalManagedWindow extends InternalWindow implements ManagedWindow {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -3389618500246332016L;

	private static CursorRepository cursorRepository = new CursorRepository();
	/** Window draw listeners. */
	private final List<WindowDragListener> dragListeners = new ArrayList<WindowDragListener>(1);

	private Point dragStart;
	private boolean movable = true;

	/**
	 * Create an InternalManagedWindow.
	 *
	 * @param handle identifier for the window manager
	 * @param title window title
	 */
	public InternalManagedWindow(String handle, String title) {
		super(title);
		/*
		 * Abusing AWT methods to pass information to WtWindowManager. The
		 * method is practically undocumented and seems to exist for storing
		 * identifiers to the components (ie. exactly what we need the name
		 * for). Anyway, it does not seem to break anything and doing this
		 * otherwise would require breaking WtWindowManager interface.
		 */
		setName(handle);
		/*
		 * Do not steal the keyboard focus. Users may be confused how
		 * to get it back.
		 */
		setFocusable(false);

		// Listeners for moving the window
		ClickListener clickListener = new ClickListener();
		DragListener dragListener = new DragListener();

		getTitlebar().addMouseListener(clickListener);
		getTitlebar().addMouseMotionListener(dragListener);
		/*
		 * Add the movement start listener to the whole window as well. This
		 * provides a convenience access for moving or raising the window. Note
		 * that even if it's decided that the behavior is annoying, it's
		 * necessary to add some MouseListener (a dummy one, if so wanted)
		 * to the window itself. Otherwise the click is passed to the game area
		 * below, which is almost certainly not what the player wants.
		 */
		addMouseListener(clickListener);
		addMouseMotionListener(dragListener);

		setCursor(cursorRepository.get(StendhalCursor.NORMAL));
	}

	@Override
	public boolean moveTo(int x, int y) {
		setLocation(x, y);
		return true;
	}

	@Override
	public void setMinimized(boolean minimized) {
		super.setMinimized(minimized);
		/*
		 * We are handling our own size management, so so we need to take care
		 * of the new bounds.
		 */
		setSize(getPreferredSize());
		relocate(getLocation());
		WtWindowManager.getInstance().setMinimized(this, minimized);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		/*
		 * Getting the remembered window formatting is not safe until all the
		 * components are in place. Also the window needs to be capable of
		 * setting its location (if on GameScreen)
		 */
		WtWindowManager.getInstance().formatWindow(this);
	}

	/**
	 * Raise the window if possible-
	 */
	void raise() {
		final Container parent = getParent();

		if (parent instanceof JLayeredPane) {
			((JLayeredPane) parent).moveToFront(InternalManagedWindow.this);
		}
	}

	/**
	 * Make the window movable or unmovable by the user. Even unmovable users
	 * obey window locations from the window manager.
	 *
	 * @param movable
	 */
	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	/**
	 * Center the window within the parent component.
	 */
	protected void center() {
		Container parent = getParent();
		final Dimension size = getPreferredSize();

		setBounds((parent.getWidth() - size.width) / 2,
				(parent.getHeight() - size.height) / 2, size.width, size.height);
	}


	/**
	 * Set the location of the window so that it keeps itself within the bounds
	 * of the parent.
	 *
	 * @param point suggested location. The actual location can differ if the
	 * 	window would not fit fully within the parent
	 */
	private void relocate(Point point) {
		Container parent = getParent();

		// Defensive copy to avoid modifying the raw coordinates. They're needed
		// by the caller.
		point = new Point(point);
		Insets insets = parent.getInsets();
		// Keep inside parent component
		point.x = Math.min(point.x, parent.getWidth() - getWidth() - insets.right);
		point.x = Math.max(point.x, insets.left);

		point.y = Math.min(point.y, parent.getHeight() - getHeight() - insets.bottom);
		point.y = Math.max(point.y, insets.top);

		setLocation(point);
		// Store the window location
		WtWindowManager.getInstance().moveTo(this, getX(), getY());
	}

	/**
	 * Start dragging the window.
	 *
	 * @param point starting point
	 */
	private void startDrag(Point point) {
		if (movable) {
			dragStart = point;
			for (WindowDragListener listener : dragListeners) {
				listener.startDrag(this);
			}
		}
	}

	/**
	 * End dragging the window.
	 */
	private void endDrag() {
		dragStart = null;
		for (WindowDragListener listener : dragListeners) {
			listener.endDrag(this);
		}
	}

	/**
	 * Drag the window.
	 *
	 * @param point mouse location
	 */
	private void drag(Point point) {
		if (dragStart != null) {
			Component parent = getParent();

			// calculate the total moved distance
			point = SwingUtilities.convertPoint(this, point, parent);
			point.x -= dragStart.x;
			point.y -= dragStart.y;

			relocate(point);
			for (WindowDragListener listener : dragListeners) {
				listener.windowDragged(this, point);
			}
		}
	}

	/**
	 * Listener for the title bar clicks.
	 */
	private class ClickListener extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent ev) {
			if (ev.getButton() == MouseEvent.BUTTON1) {
				startDrag(ev.getPoint());
			}
		}

		@Override
		public void mouseReleased(final MouseEvent ev) {
			// Only call endDrag() if there was actually an active drag
			if ((ev.getButton() == MouseEvent.BUTTON1) && (dragStart != null)) {
				endDrag();
			}
		}

		@Override
		public void mouseClicked(final MouseEvent ev) {
			if (ev.getButton() == MouseEvent.BUTTON1) {
				/*
				 * Raise the window if possible.
				 */
				raise();
			}
		}
	}

	/**
	 * Listened for title bar dragging.
	 */
	private class DragListener extends MouseMotionAdapter {
		@Override
		public void mouseDragged(final MouseEvent ev) {
			drag(ev.getPoint());
		}
	}

	/**
	 * Add a window drag listener. Added listeners will be notified if this
	 * window is dragged by the user.
	 *
	 * @param listener added listener
	 */
	public void addWindowDragListener(WindowDragListener listener) {
		dragListeners.add(listener);
	}

	/**
	 * Interface for listening to dragging the window by mouse.
	 */
	public interface WindowDragListener {
		/**
		 * Called when the user initiates a window drag.
		 *
		 * @param component dragged component
		 */
		void startDrag(Component component);
		/**
		 * Called when the user ends a window drag.
		 *
		 * @param component dragged component
		 */
		void endDrag(Component component);
		/**
		 * Called when the user drags a window.
		 *
		 * @param component dragged component
		 * @param point the location of the drag. This is not necessarily the
		 * 	new coordinates of the window.
		 */
		void windowDragged(Component component, Point point);
	}
}
