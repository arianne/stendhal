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
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.User;
import marauroa.common.game.RPObject;

/**
 * A window for showing contents of an entity's slot in a grid of ItemPanels
 */
public class SlotWindow extends InternalManagedWindow implements Inspectable {

	private static Logger logger = Logger.getLogger(SlotWindow.class);

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
	public SlotWindow(final String title, final int width, final int height) {
		super(title, title);

		content = new SlotGrid(width, height);
		setContent(content);
	}

	protected void setSlotsLayout(final int width, final int height) {
		content.setSlotsLayout(width, height);
		setContent(content);

		final String slotName = content.getSlotName();
		if (parent != null && slotName != null) {
			content.setSlot(parent, slotName);
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				revalidate();
				repaint();
			}
		});

		// workaround to update component sizes
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Component window = SwingUtilities.getRoot(getParent());
						window.doLayout();
						window.revalidate();
						window.repaint();
					}
				});
			}
		}).start();
	}

	/**
	 * Set the types the panels can accept.
	 *
	 * @param types accepted types
	 */
	@SafeVarargs
	public final void setAcceptedTypes(Class<? extends IEntity> ... types) {
		content.setAcceptedTypes(types);
	}

	/**
	 * Sets the parent entity of the window.
	 *
	 * @param parent entity owning the slot presented by the window
	 * @param slot slot presented
	 */
	public void setSlot(final IEntity parent, final String slot) {
		this.parent = parent;
		content.setSlot(parent, slot);
	}

	/**
	 * Set the inspector used for the contained entities.
	 *
	 * @param inspector used inspector
	 */
	@Override
	public void setInspector(Inspector inspector) {
		content.setInspector(inspector);
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

	@Override
	public void close() {
		content.release();
		super.close();
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

			// Check if the parent is user
			RPObject root = parent.getRPObject().getBaseContainer();
			// We don't want to close our own stuff
			// The root entity may have been removed, but still if it was
			// the user we do not want to close it.
			// User may have been changed by the main thread, so we can not rely
			// on user.getRPObject() being equal to root. (bug #3159058)
			final String type = root.getRPClass().getName();
			if (type.equals("player") && root.has("name")) {
				if (StendhalClient.get().getCharacter().equalsIgnoreCase(
						root.get("name"))) {
					return true;
				}
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
