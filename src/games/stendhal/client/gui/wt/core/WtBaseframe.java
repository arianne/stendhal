/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Frame.java
 *
 * Created on 18. Oktober 2005, 19:40
 */

package games.stendhal.client.gui.wt.core;

import games.stendhal.client.IGameScreen;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPopupMenu;

/**
 * Frame is the main gui container. It spans the whole screen and does not have
 * a parent.
 * 
 * This is the main glue to AWT/Swing event handling. All AWT-Events are
 * preprocessed here and forwarded to the clients.
 * 
 * Note: This object is thread safe.
 * 
 * @author mtotz
 */
public class WtBaseframe extends WtPanel implements MouseListener,
		MouseMotionListener {

	/** The currently dragged object or null if there is no such drag operation. */
	private WtDraggable draggedObject;

	/** The point where the drag started. */
	private Point dragStartPoint;

	/** True when there is a dragging operation in progress. */
	private boolean dragInProgress;

	/** The context menu, if there is one. */
	protected JPopupMenu jcontextMenu;

	/** A flag for tracking ContextMenu changes. */
	private boolean recreatedContextMenu;

	/**
	 * True if the last single click was handled; it can't be a part of a double
	 * click.
	 */
	private boolean lastClickWasHandled;

	/** The time at which a mouse button was last pressed. */
	private long timeOfLastMousePress = System.currentTimeMillis();

	/**
	 * Create the root Wt frame.
	 * 
	 * @param width
	 *            The frame width (in pixels).
	 * @param height
	 *            The frame height (in pixels).
	 * @param gameScreen
	 */
	public WtBaseframe(final int width, final int height, final IGameScreen gameScreen) {
		super("baseframe", 0, 0, width, height, gameScreen);
		setFrame(false);
		setTitleBar(false);
		setMinimizeable(false);
		setCloseable(false);
		setMovable(false);
	}

	/** Resizing is disabled. */
	@Override
	public void resizeToFitClientArea(final int width, final int height) {
	}

	/** @return the currently dragged object or null, if there is none. */
	public synchronized WtDraggable getDraggedObject() {
		// currently no drag operation?
		if (!dragInProgress || (draggedObject == null)) {
			return null;
		}

		// we handle all panels ourself
		if (draggedObject instanceof WtPanel) {
			return null;
		}

		// return the object
		return draggedObject;
	}

	/**
	 * Sets the context menu. It is closed automatically once the user clicks
	 * outside of it.
	 */
	@Override
	public void setContextMenu(final JPopupMenu jcontextMenu) {
		if (this.jcontextMenu != null) {
			this.jcontextMenu.setVisible(false);
		}

		this.jcontextMenu = jcontextMenu;
		recreatedContextMenu = true;
	}

	/**
	 * Draws the frame into the graphics object.
	 * 
	 * @param g
	 *            graphics where to render to
	 */
	@Override
	public synchronized void draw(final Graphics2D g) {
		// draw the stuff
		super.draw(g);

		// do we have a dragged object?
		if (dragInProgress && (draggedObject != null)) {
			// translate graphics to local start of the dragged object
			final Graphics dragg = g.create();
			dragg.translate(dragStartPoint.x, dragStartPoint.y);
			// yep, draw it
			draggedObject.drawDragged(dragg);
			dragg.dispose();
		}
	}

	/**
	 * Stops the dragging operations.
	 * 
	 * @param e
	 */
	private void stopDrag(final MouseEvent e) {
		// be sure to stop dragging operations when the left button is released
		if (dragInProgress && (draggedObject != null)) {
			final Point p = e.getPoint();
			draggedObject.dragFinished(p);
			// now check if there is a drop-target direct under the mouse cursor
			checkDropped(p.x, p.y, draggedObject);
		}
		dragInProgress = false;
	}

	/**
	 * Invoked when the mouse enters a component. This event is ignored.
	 * 
	 * @param e
	 */
	public synchronized void mouseEntered(final MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component. This event is ignored.
	 * 
	 * @param e
	 */
	public synchronized void mouseExited(final MouseEvent e) {
	}

	private boolean rightMouseButtonPressed;

	/**
	 * Invoked when a mouse button has been pressed on a component. This event
	 * is ignored.
	 * 
	 * @param e
	 */
	public synchronized void mousePressed(final MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButtonPressed = true;
		}

		timeOfLastMousePress = System.currentTimeMillis();
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e
	 */
	public synchronized void mouseReleased(final MouseEvent e) {
		// be sure to stop dragging operations when the left button is released
		if (e.getButton() == MouseEvent.BUTTON1) {
			stopDrag(e);
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

	/**
	 * Java's official mouseClick handler; we don't use this because it doesn't
	 * register clicks while the mouse is moving at all.
	 * 
	 * @param e
	 */
	public synchronized void mouseClicked(final MouseEvent e) {
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 * a component. This event is propagated to all children.
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void onMouseClick(final MouseEvent e) {
		final Point p = e.getPoint();
		recreatedContextMenu = false;

		// Added support for ctrl + click for Mac OS X intensifly@gmx.com

		final int onmask = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().contains("os x")
				&& ((e.getModifiersEx() & onmask) == onmask)) {
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

		if (recreatedContextMenu) {
			/*
			 * A context menu was added
			 */
			if (jcontextMenu != null) {
				jcontextMenu.setLightWeightPopupEnabled(false);

				jcontextMenu.show(e.getComponent(), e.getX() - 10,
						e.getY() - 10);
			}

			recreatedContextMenu = false;
		} else {
			/*
			 * whatever the click was...delete the context menu (if it wasn't
			 * recreated during the callbacks)
			 */
			if (jcontextMenu != null) {
				jcontextMenu.setVisible(false);
				jcontextMenu = null;
			}
		}
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * This event will find the panel just under the mouse cursor and starts to
	 * drag (if the panel allows it)
	 * 
	 * @param e
	 */
	public synchronized void mouseDragged(final MouseEvent e) {
		final Point p = e.getPoint();

		if (rightMouseButtonPressed) {
			// Disallow dragging with right button.
			return;
		}

		if (!dragInProgress) {
			draggedObject = getDragged(p);

			// did we get an object
			if (draggedObject != null) {
				// do the object want to be dragged
				if (draggedObject.dragStarted()) {
					// start drag
					dragStartPoint = e.getPoint();
				} else {
					// dragging disabled
					draggedObject = null;
				}
			}
			// drag is started anyway, even when there is no object to move
			dragInProgress = true;
		} else if (draggedObject != null) {
			// drag resumed...inform the dragged object
			p.translate(-dragStartPoint.x, -dragStartPoint.y);
			draggedObject.dragMoved(p);
		}
	}

	/**
	 * Invoked when the mouse cursor has been moved onto a component but no
	 * buttons have been pushed.
	 * 
	 * This event stops all dragging operations.
	 * 
	 * @param e
	 */
	public synchronized void mouseMoved(final java.awt.event.MouseEvent e) {
		// be sure to stop dragging operations
		stopDrag(e);
	}

	/** disabled. */
	@Override
	public void setName(final String name) {
	}

	/** disabled. */
	@Override
	public void setTitleBar(final boolean titleBar) {
	}

	/** disabled. */
	@Override
	public void setFrame(final boolean frame) {
	}

	/** disabled. */
	@Override
	public void setCloseable(final boolean minimizeable) {
	}
}
