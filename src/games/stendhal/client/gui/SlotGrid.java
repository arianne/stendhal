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

import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.ContentChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A view of an RPSlot in a grid of ItemPanels.
 */
public class SlotGrid extends JComponent implements ContentChangeListener, Inspectable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -1822952960582728997L;

	private static final int PADDING = 1;
	private static final Logger logger = Logger.getLogger(SlotGrid.class);
	
	/** All shown item panels */
	private final List<ItemPanel> panels;
	/** The parent entity of the shown slot */
	private IEntity parent;
	/** Name of the shown slot */
	private String slotName;

	
	public SlotGrid(int width, int height) {
		setLayout(new GridLayout(height, width, PADDING, PADDING));
		panels = new ArrayList<ItemPanel>();
		
		for (int i = 0; i < width * height; i++) {
			ItemPanel panel = new ItemPanel(null, null);
			panel.setItemNumber(i);
			panels.add(panel);
			add(panel);
		}
	}
	
	/**
	 * Set the types the panels can accept.
	 * 
	 * @param types
	 */
	public void setAcceptedTypes(Class<? extends IEntity> ... types) {
		// Reuse the same set for all the panels
		List<Class<? extends IEntity>> list = Arrays.asList(types);
		for (ItemPanel panel : panels) {
			panel.setAcceptedTypes(list);
		}
	}
	
	/**
	 * Sets the parent entity of the window.
	 * 
	 * @param parent
	 * @param slot
	 */
	public void setSlot(final IEntity parent, final String slot) {
		if (!GameLoop.isGameLoop()) {
			// Game loop can modify slot contents at will, so it's not a good
			// idea to try to read the contents in the EDT.
			GameLoop.get().runOnce(new Runnable() {
				@Override
				public void run() {
					setSlot(parent, slot);
				}
			});
			return;
		}
		
		if (this.parent != null) {
			this.parent.removeContentChangeListener(this);
		}
		
		this.parent = parent;
		this.slotName = slot;

		/*
		 * Reset the container info for all holders
		 */
		for (final ItemPanel panel : panels) {
			panel.setParent(parent);
			panel.setName(slot);
		}

		parent.addContentChangeListener(this);
		scanSlotContent();
	}
	
	/**
	 * Get the name of the slot this grid represents
	 * 
	 * @return name of the slot
	 */
	public String getSlotName() {
		return slotName;
	}
	
	/**
	 * Set the inspector the contained entities should use.
	 * 
	 * @param inspector
	 */
	@Override
	public void setInspector(Inspector inspector) {
		for (ItemPanel panel : panels) {
			panel.setInspector(inspector);
		}
	}
	
	/**
	 * Clear the grid and detach it from the slot it shows.
	 */
	void release() {
		if (parent != null) {
			parent.removeContentChangeListener(SlotGrid.this);
		}
		// Ensure that parent & slotName do not change in the middle of
		// scanSlotContent() 
		GameLoop.get().runOnce(new Runnable() {
			@Override
			public void run() {
				for (final ItemPanel panel : panels) {
					panel.setEntity(null);
				}
				parent = null;
				slotName = null;
			}
		});
	}
	
	/**
	 * Scans the content of the slot.
	 */
	private void scanSlotContent() {
		if ((parent == null) || (slotName == null)) {
			return;
		}

		// Clear the panels, in case they are not already empty
		for (ItemPanel panel : panels) {
			panel.setEntity(null);
		}
		final RPSlot rpslot = parent.getSlot(slotName);
		// Treat the entire slot contents as a content change
		contentAdded(rpslot);
	}

	@Override
	public void contentAdded(RPSlot added) {
		// We are interested only in one slot
		if (slotName.equals(added.getName())) {
			for (RPObject obj : added) {
				handleAdded(obj);
			}
		}
	}
	
	/**
	 * Handle an added or modified item.
	 * 
	 * @param obj changed or added object
	 */
	private void handleAdded(RPObject obj) {
		ID id = obj.getID();
		for (ItemPanel panel : panels) {
			IEntity entity = panel.getEntity();
			if (entity != null && id.equals(entity.getRPObject().getID())) {
				// Changed rather than added.
				return;
			}
		}
		// Actually added. Get the corresponding entity
		IEntity entity = GameObjects.getInstance().get(obj);
		
		// Tuck it in the first free slot
		for (ItemPanel panel : panels) {
			if (panel.getEntity() == null) {
				panel.setEntity(entity);
				return;
			}
		}
		
		logger.error("More objects than slots: " + slotName);
	}

	@Override
	public void contentRemoved(RPSlot removed) {
		// We are interested only in one slot
		if (slotName.equals(removed.getName())) {
			for (RPObject obj : removed) {
				ID id = obj.getID();
				for (ItemPanel panel : panels) {
					IEntity entity = panel.getEntity();
					if (entity != null && id.equals(entity.getRPObject().getID())) {
						if (obj.size() == 1) {
							// The object was removed
							panel.setEntity(null);
							continue;
						}
					}
				}
			}
			compressSlots();
		}
	}
	
	/**
	 * Shift item panel contents so that the empty ones are last.
	 */
	private void compressSlots() {
		Iterator<ItemPanel> emptyIt = panels.iterator();
		Iterator<ItemPanel> fullIt = panels.iterator();
		// fullIt is always at least as far as emptyIt
		while (fullIt.hasNext()) {
			ItemPanel full = fullIt.next();
			ItemPanel empty = emptyIt.next();
			if (empty.getEntity() == null) {
				// Found an empty slot. Try to move an entity from some filled
				// one after it to it. Find the next filled slot.
				while (full.getEntity() == null) {
					if (fullIt.hasNext()) {
						full = fullIt.next();
					} else {
						// Finished scanning the slots
						return;
					}
				}
				// Found a filled slot. Move the entity to the empty one
				full.moveViewTo(empty);
			}
		}
	}
}
