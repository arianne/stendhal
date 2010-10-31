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

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 * A window for showing contents of an entity's slot in a grid of ItemPanels
 */
public class SlotWindow extends InternalManagedWindow {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 7599313996868476471L;

	/**
	 * when the player is this far away from the container, the panel is closed.
	 */
	private static final int MAX_DISTANCE = 4;
	
	private final SlotGrid content;
	private IEntity parent;
	
	/**
	 * Create a new EntityContainer.
	 * 
	 * @param title window title
	 * @param width number of slot columns
	 * @param height number of slot rows
	 */
	public SlotWindow(String title, int width, int height) {
		super(title, title);
		
		content = new SlotGrid(width, height);
		setContent(content);
	}
	
	/**
	 * Sets the parent entity of the window.
	 * 
	 * @param parent
	 * @param slot
	 */
	public void setSlot(final IEntity parent, final String slot) {
		this.parent = parent;
		content.setSlot(parent, slot);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		/*
		 * This needs to be done in paint(), not in paintComponent (as far as
		 * the check is done at paint time). paintComponent does not necessarily
		 * get called at all due to InternalWindow using cached drawing. 
		 */
		checkDistance();
	}
	
	/**
	 * Check the distance of the player to the base item. When the player is too
	 * far away, this panel closes itself.
	 */
	private void checkDistance() {
		if (!isCloseEnough()) {
			close();
		}
	}
	
	/**
	 * Check if the user is close enough the parent entity of the slot. If
	 * the user is too far away the window should not be opened, and it should
	 * be closed if it was already open.
	 * 
	 * @return <code>true</code> if the user is close enough to have the window
	 * 	open, <code>false</code> otherwise.
	 */
	public boolean isCloseEnough() {
		final User user = User.get();

		if ((user != null) && (parent != null)) {
			// null checks are fixes for Bug 1825678:
			// NullPointerException happened
			// after double clicking one
			// monster and a fast double
			// click on another monster
			if (parent.isUser()) {
				// We don't want to close our own stuff
				return true;
			}

			return isCloseEnough(user.getX(), user.getY());
		}

		return true;
	}
	
	/**
	 * Check if the user is close enough the parent entity of the slot. If
	 * the user is too far away the window should not be opened, and it should
	 * be closed if it was already open.
	 * 
	 * @param x x coordinate of the user
	 * @param y y coordinate of the user
	 * @return <code>true</code> if the user is close enough to have the window
	 * 	open, <code>false</code> otherwise.
	 */
	private boolean isCloseEnough(final double x, final double y) {
		final int px = (int) x;
		final int py = (int) y;

		final Rectangle2D orig = parent.getArea();
		orig.setRect(orig.getX() - MAX_DISTANCE, orig.getY() - MAX_DISTANCE,
				orig.getWidth() + MAX_DISTANCE * 2, orig.getHeight()
						+ MAX_DISTANCE * 2);

		return orig.contains(px, py);
	}
}
