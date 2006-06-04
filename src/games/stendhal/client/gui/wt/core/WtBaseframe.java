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

import games.stendhal.client.GameScreen;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
	/** the currently dragged object or null if there is no such drag operation */
	private WtDraggable draggedObject;

	/** the point where the drag started */
	private Point dragStartPoint;

	/** true when there is a dragging operation in progress */
	private boolean dragInProgress;

	/** the context menu, if there is one */
	private WtList contextMenu;

	/** a flag for tracking ContextMenu changes */
	private boolean recreatedContextMenu;

	/** Creates the Frame from the given GameScreen instance */
	public WtBaseframe(GameScreen screen) {
		super("baseframe", 0, 0, screen.getWidthInPixels(), screen
				.getHeightInPixels());
		setFrame(false);
		setTitleBar(false);
		setMinimizeable(false);
		setCloseable(false);
		setMoveable(false);
	}

	/** resizing is disabled */
	public void resizeToFitClientArea(int width, int height) {
	}

	/** returns the currently dragged object or null if there is none */
	public synchronized WtDraggable getDraggedObject() {
		// currently no drag operation?
		if (!dragInProgress || draggedObject == null)
			return null;

		// we handle all panels ourself
		if (draggedObject instanceof WtPanel)
			return null;

		// return the object
		return draggedObject;
	}

	/**
	 * Sets the context menu. It is closed automatically one the user clicks.
	 * outside of it.
	 */
	public void setContextMenu(WtList contextMenu) {
		if (this.contextMenu != null) {
			this.contextMenu.close();
		}
		this.contextMenu = contextMenu;
		this.contextMenu.setParent(this);
		recreatedContextMenu = true;
	}

	/**
	 * draws the frame into the graphics object
	 * 
	 * @param g
	 *            graphics where to render to
	 * @return same graphics object
	 */
	public synchronized Graphics draw(Graphics g) {
		// draw the stuff
		super.draw(g);

		// draw the context menu if we have one
		if (contextMenu != null) {
			contextMenu.draw(g);
		}

		// do we have a dragged object?
		if (dragInProgress && draggedObject != null) {
			// translate graphics to local start of the dragged object
			Graphics dragg = g.create();
			dragg.translate(dragStartPoint.x, dragStartPoint.y);
			// yep, draw it
			draggedObject.drawDragged(dragg);
		}
		return g;
	}

	/** stops the dragging operations */
	private void stopDrag(MouseEvent e) {
		// be sure to stop dragging operations when theleft button is released
		if (dragInProgress && draggedObject != null) {
			Point p = e.getPoint();
			draggedObject.dragFinished(p);
			// now check if there is a drop-target direct unter the mouse cursor
			checkDropped(p.x, p.y, draggedObject);
		}
		dragInProgress = false;
	}

	/** Invoked when the mouse enters a component. This event is ignored. */
	public synchronized void mouseEntered(MouseEvent e) {
	}

	/** Invoked when the mouse exits a component. This event is ignored. */
	public synchronized void mouseExited(MouseEvent e) {
	}

	private boolean rightMouseButtonPressed;

	/**
	 * Invoked when a mouse button has been pressed on a component. This event
	 * is ignored.
	 */
	public synchronized void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButtonPressed = true;
		}
	}

	/** Invoked when a mouse button has been released on a component. */
	public synchronized void mouseReleased(MouseEvent e) {
		// be sure to stop dragging operations when the left button is released
		if (e.getButton() == MouseEvent.BUTTON1) {
			stopDrag(e);
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButtonPressed = false;
		}
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 * a component. This event is propagated to all childs.
	 * 
	 * @param e
	 *            the mouse event
	 */
	public synchronized void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		recreatedContextMenu = false;

		// Added support for ctrl + click for Mac OS X intensifly@gmx.com

		int onmask = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().contains("os x")
				&& (e.getModifiersEx() & onmask) == onmask) {
			onMouseRightClick(p);
		}

		else if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 1) {
				boolean contextMenuClicked = false;
				// do we have a context menu?
				if (contextMenu != null && !contextMenu.isClosed()) {
					// yep, so inform it of the mouse click
					Point other = new Point(p);
					other.translate(-contextMenu.getX(), -contextMenu.getY());
					contextMenuClicked = contextMenu.onMouseClick(other);
				}

				// process the rest if the context menu wasn't clicked
				if (!contextMenuClicked) {
					onMouseClick(p);
				}
			} else if (e.getClickCount() == 2) {
				onMouseDoubleClick(p);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// no double rightclick supported
			onMouseRightClick(p);
		}

		// whatever the click was...delete the context menu (if it wasn't
		// recreated
		// during the callbacks)
		if (contextMenu != null && !recreatedContextMenu) {
			contextMenu.setParent(null);
			contextMenu.close();
			contextMenu = null;
		}
		recreatedContextMenu = false;
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * This event will find the panel just under the mouse cursor and starts to
	 * drag (if the panel allows it)
	 */
	public synchronized void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();

		if (rightMouseButtonPressed) {
			// Disallow dragging with right button.
			return;
		}

		if (!dragInProgress) {
			draggedObject = getDragged(p);

			// did we get an object
			if (draggedObject != null) {
				// do the object want to be dragged
				if (!draggedObject.dragStarted()) {
					// dragging disabled
					draggedObject = null;
				} else {
					// start drag
					dragStartPoint = e.getPoint();
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
	 * This event stopps all dragging operations.
	 */
	public synchronized void mouseMoved(java.awt.event.MouseEvent e) {
		// be sure to stop dragging operations
		stopDrag(e);
	}

	/** disabled */
	public void setName(String name) {
	}

	/** disabled */
	public void setTitleBar(boolean titleBar) {
	}

	/** disabled */
	public void setFrame(boolean frame) {
	}

	/** disabled */
	public void setCloseable(boolean minimizeable) {
	}
}
