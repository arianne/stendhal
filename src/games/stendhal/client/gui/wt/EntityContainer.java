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
package games.stendhal.client.gui.wt;

import games.stendhal.client.GameObjects;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityFactory;
import games.stendhal.client.entity.User;
import games.stendhal.client.events.PositionChangeListener;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This panel is a container showing all items in a slot
 * 
 * @author mtotz
 */
public class EntityContainer extends WtPanel implements PositionChangeListener {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(EntityContainer.class);

	/** when the player is this far away from the container, the panel is closed */
	private static final int MAX_DISTANCE = 4;

	/** the panels for each item */
	private List<EntitySlot> slotPanels;

	/** the object which has the slot */
	private Entity parent;

	/** the slots name */
	private String slotName;

	private RPSlot shownSlot;

	/** creates the panel */
	public EntityContainer(StendhalClient client, String name, int width,
			int height) {
		super(name, 0, 300, 100, 100);

		setTitletext(name);
		setTitleBar(true);
		setFrame(true);
		setMinimizeable(true);
		setCloseable(true);
		shownSlot = null;

		int spriteWidth = EntitySlot.getDefaultWidth();
		int spriteHeight = EntitySlot.getDefaultHeight();

		slotPanels = new ArrayList<EntitySlot>(width * height);

		// add the slots
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				EntitySlot entitySlot = new EntitySlot(client, name, null, x
						* spriteWidth + x, y * spriteHeight + y);
				slotPanels.add(entitySlot);
				addChild(entitySlot);
			}
		}

		// resize panel
		resizeToFitClientArea(width * spriteWidth + (width - 1), height
				* spriteHeight + (height - 1));
	}

	/** we're using the window manager */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/** rescans the content of the slot */
	private void rescanSlotContent() {
		if ((parent == null) || (slotName == null)) {
			return;
		}

		RPSlot rpslot = parent.getSlot(slotName);

		// Skip if not changed
		if ((shownSlot != null) && shownSlot.equals(rpslot)) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("DIFFERENT");
			logger.debug("SHOWN: " + shownSlot);
			logger.debug("ORIGINAL: " + rpslot);
		}

		GameObjects gameObjects = GameObjects.getInstance();

		Iterator<EntitySlot> iter = slotPanels.iterator();

		/*
		 * Fill from contents
		 */
		if (rpslot != null) {
			shownSlot = (RPSlot) rpslot.clone();

			for (RPObject object : shownSlot) {
				if (!iter.hasNext()) {
					logger.error("More objects than slots: " + slotName);
					break;
				}

				Entity entity = gameObjects.get(object);

				if (entity == null) {
					logger.warn("Unable to find entity for: " + object,
							new Throwable("here"));
					entity = EntityFactory.createEntity(object);
				}

				iter.next().setEntity(entity);
			}
		} else {
			shownSlot = null;
			logger.error("No slot found: " + slotName);
		}

		/*
		 * Clear remaining holders
		 */
		while (iter.hasNext()) {
			iter.next().setEntity(null);
		}
	}

	/**
	 * Check the distance of the player to the base item. When the player is too
	 * far away, this panel closes itself.
	 * 
	 * TODO: Change to event model, rather than polling
	 */
	private void checkDistance() {
		User user = User.get();

		if (user != null && parent != null) { // fix for Bug 1825678:
												// NullPointerException happened
												// after double clicking one
												// monster and a fast double
												// click on another monster
			if (user.getID().equals(parent.getID())) {
				// We don't want to close our own stuff
				return;
			}

			positionChanged(user.getX(), user.getY());
		}
	}

	/*
	 * Clear all holders.
	 */
	protected void clear() {
		for (EntitySlot entitySlot : slotPanels) {
			entitySlot.setEntity(null);
		}

		shownSlot = null;
	}

	/** sets the player entity */
	public void setSlot(Entity parent, String slot) {
		this.parent = parent;
		this.slotName = slot;

		/*
		 * Reset the container info for all holders
		 */
		for (EntitySlot entitySlot : slotPanels) {
			entitySlot.setParent(parent);
			entitySlot.setName(slot);
		}

		shownSlot = null;
		rescanSlotContent();
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(Graphics2D g) {
		rescanSlotContent();
		super.drawContent(g);

		// TODO: Change to event model, rather than polling
		checkDistance();
	}

	/**
	 * Close the panel.
	 */
	@Override
	public void close() {
		clear();
		super.close();
	}

	/**
	 * Destroy the panel.
	 */
	@Override
	public void destroy() {
		clear();
		parent = null;

		super.destroy();
	}

	//
	// PositionChangeListener
	//

	/**
	 * The user position changed.
	 * 
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	public void positionChanged(final double x, final double y) {
		/*
		 * Check if the user has moved too far away
		 */
		int px = (int) x;
		int py = (int) y;

		int ix = (int) parent.getX();
		int iy = (int) parent.getY();

		Rectangle2D orig = parent.getArea();
		orig.setRect(orig.getX() - MAX_DISTANCE, orig.getY() - MAX_DISTANCE,
				orig.getWidth() + MAX_DISTANCE * 2, orig.getHeight()
						+ MAX_DISTANCE * 2);

		if (!orig.contains(px, py)) {
			logger.debug("Closing " + slotName + " container because " + px
					+ "," + py + " is too far from (" + ix + "," + iy + "):"
					+ orig);
			destroy();
		}
	}
}
