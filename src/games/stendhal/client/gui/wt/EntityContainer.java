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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityFactory;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This panel is a container showing all items in a slot
 *
 * @author mtotz
 */
public class EntityContainer extends WtPanel {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(EntityContainer.class);

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
			}
		}

		// resize panel
		this.resizeToFitClientArea(width * spriteWidth + (width - 1), height
				* spriteHeight + (height - 1));

		for (EntitySlot entitySlot : slotPanels) {
			addChild(entitySlot);
		}
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

		if (rpslot == null) {
			/*
			 * Clear all slots
			 */
			for (EntitySlot entitySlot : slotPanels) {
				entitySlot.setEntity(null);
			}

			// TODO: fix the non existing "keyring slot" for old server
			return;
		}

		shownSlot = (RPSlot) rpslot.clone();

		Iterator<RPObject> it = shownSlot.iterator();

		for (EntitySlot entitySlot : slotPanels) {
			// be sure to update the name
			entitySlot.setName(shownSlot.getName());

			// tell 'em the the parent
			entitySlot.setParent(parent);

			// Set the entity
			if (it.hasNext()) {
				entitySlot.setEntity(EntityFactory.createEntity(it.next()));
			} else {
				entitySlot.setEntity(null);
			}
		}
	}

	/**
	 * Check the distance of the player to the base item. When the player is too
	 * far away, this panel closes itself. Note that this is clientside only.
	 */
	private void checkDistance() {
		int px = (int) User.get().getX();
		int py = (int) User.get().getY();
		int ix = (int) parent.getX();
		int iy = (int) parent.getY();

		if (User.get().getID().equals(parent.getID())) {
			// We don't want to close our own stuff
			return;
		}

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

	/** sets the player entity */
	public void setSlot(Entity parent, String slot) {
		this.parent = parent;
		this.slotName = slot;
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
		if ((parent != null) && (slotName != null) && !isClosed()) {
			RPSlot rpslot = parent.getSlot(slotName);
			// rescan the content if the size changes
			if ((shownSlot == null) || !shownSlot.equals(rpslot)) {
				logger.debug("DIFFERENT");
				logger.debug("SHOWN: " + shownSlot);
				logger.debug("ORIGINAL: " + rpslot);
				rescanSlotContent();
			} else {
				if (parent instanceof games.stendhal.client.entity.Corpse) {
					logger.debug("EQUAL");
					logger.debug("SHOWN: " + shownSlot);
					logger.debug("ORIGINAL: " + rpslot);
				}
			}
			checkDistance();
		}

		super.drawContent(g);
	}
}
