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

import games.stendhal.client.*;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * This panel is a container showing all items in a slot
 * 
 * @author mtotz
 */
public class EntityContainer extends WtPanel {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(EntityContainer.class);

	/** when the player is this far away from the container, the panel is closed */
	private static final int MAX_DISTANCE = 5;

	/** the panels for each item */
	private List<EntitySlot> slotPanels;

	/** the object which has the slot */
	private Entity parent;

	/** the slots name */
	private String slotName;

	private RPSlot shownSlot;

	/** creates the panel */
	public EntityContainer(GameObjects gameObjects, String name, int width,
			int height) {
		super(name, 0, 300, 100, 100);
		setTitletext(name);
		setTitleBar(true);
		setFrame(true);
		setMinimizeable(true);
		setCloseable(true);
		shownSlot = null;

		SpriteStore st = SpriteStore.get();
		Sprite slotSprite = st.getSprite("data/gui/slot.png");

		int spriteWidth = slotSprite.getWidth();
		int spriteHeight = slotSprite.getHeight();

		slotPanels = new ArrayList<EntitySlot>();
		// add the slots
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				EntitySlot entitySlot = new EntitySlot(name, slotSprite, x
						* spriteWidth + x, y * spriteHeight + y, gameObjects);
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
		shownSlot = (RPSlot) rpslot.clone();

		Iterator<RPObject> it = (rpslot != null) ? shownSlot.iterator() : null;

		for (EntitySlot entitySlot : slotPanels) {
			// be sure to update the name
			entitySlot.setName(shownSlot.getName());
			// remove old objects
			entitySlot.clear();
			// tell 'em the the parent
			entitySlot.setParent(parent);
			// add new rpobjects
			if ((it != null) && it.hasNext()) {
				entitySlot.add(it.next());
			}
		}
	}

	/**
	 * Check the distance of the player to the base item. When the player is too
	 * far away, this panel closes itself. Note that this is clientside only.
	 */
	private void checkDistance() {
		RPObject player = StendhalClient.get().getPlayer();

		int px = player.getInt("x");
		int py = player.getInt("y");
		int ix = (int) parent.getX();
		int iy = (int) parent.getY();

		int distance = Math.abs(px - ix) + Math.abs(py - iy);

		if (player.getID().equals(parent.getID())) {
			// We don't want to close our own stuff
			return;
		}

		if (distance > MAX_DISTANCE) {
			logger.info("Closing " + slotName + " container because " + px
					+ "," + py + " is far from " + ix + "," + iy);
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
	 * draws the panel. it also checks for modified slot content
	 */
	@Override
	public Graphics draw(Graphics g) {
		if(isClosed()) {
			return g;
		}

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

		return super.draw(g);
	}

}
