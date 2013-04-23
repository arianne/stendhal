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

import games.stendhal.client.gui.styled.cursor.CursorRepository;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

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
		
		// Keep inside parent component
		if (point.x < 0) {
			point.x = 0;
		} else if ((point.x + getWidth()) > parent.getWidth()) {
			point.x = parent.getWidth() - getWidth();
		}

		if (point.y < 0) {
			point.y = 0;
		} else if ((point.y + getHeight()) > parent.getHeight()) {
			point.y = parent.getHeight() - getHeight();
		}

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
		}
	}
	
	/**
	 * End dragging the window.
	 */
	private void endDrag() {
		dragStart = null;
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
			if (ev.getButton() == MouseEvent.BUTTON1) {
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
}
