/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A SlotWindow that can be told to not close itself while it is frozen.
 * Used to keep the window open while the player is changing zone. Thawing
 * it tries to find the entity that represents the same entity on the new
 * zone, and changes to show the contents of that.
 */
class FreezableSlotWindow extends SlotWindow {
	private volatile boolean frozen;
	private List<String> path;
	
	/**
	 * Create a new FreezableSlotWindow.
	 * 
	 * @param title title of the window
	 * @param width number of slot columns
	 * @param height number of slot rows
	 */
	FreezableSlotWindow(String title, int width, int height) {
		super(title, width, height);
	}
	
	/**
	 * Prevent closing this window until it's been thawed.
	 */
	void freeze() {
		// save the object path
		path = parent.getPath();
		frozen = true;
	}
	
	/**
	 * Try to find the new entity corresponding to the old parent, and
	 * allow closing the window again.
	 */
	void thaw() {
		/*
		 * Try to find the new object that represents the same item as
		 * the old parent before zone change.
		 */
		Iterator<String> it = path.iterator();
		it.next();
		try {
			RPObject obj = User.get().getRPObject();
			while (it.hasNext()) {
				RPSlot slot = obj.getSlot(it.next());
				int id = Integer.parseInt(it.next());
				obj = slot.get(new RPObject.ID(id, (String) null));
			}
			IEntity entity = GameObjects.getInstance().get(obj);
			setSlot(entity, content.getSlotName());
		} catch (RuntimeException exc) {
			/*
			 * Something went wrong. Log the error and close the window as
			 * it's no longer valid.
			 */
			Logger.getLogger(FreezableSlotWindow.class).error("Failed to change container slot", exc);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					FreezableSlotWindow.super.close();
				}
			});
		}
		frozen = false;
	}
	
	@Override
	public void close() {
		if (!frozen) {
			super.close();
		}
	}
}