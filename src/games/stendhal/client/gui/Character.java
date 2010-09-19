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

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityFactory;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.SpriteStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JComponent;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Window for showing the equipment the player is wearing.
 */
public class Character extends InternalManagedWindow implements EntityChangeListener {
	/** Padding between the ItemPanels */
	private static final int PADDING = 1;
	/** The pixel amount the hand slots should be below the armor slot */
	private static final int HAND_YSHIFT = 10;
	
	/** ItemPanels searchable by the respective slot name */
	private final Map<String, ItemPanel> slotPanels = new HashMap<String, ItemPanel>();
	private User player;
	
	/**
	 * Create a new character window.
	 */
	public Character() {
		super("character", "Character");
		createLayout();
		// Don't allow the user close this. There's no way to get it back. 
		setCloseable(false);
	}
	
	/**
	 * sets the player entity.
	 * 
	 * @param userEntity
	 */
	public void setPlayer(final User userEntity) {
		player = userEntity;
		userEntity.addChangeListener(this);
		refreshContents();
	}
	
	/**
	 * Create the content layout and add the ItemPanels.
	 */
	private void createLayout() {
		// Layout containers
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, PADDING);
		JComponent left = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		JComponent middle = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		JComponent right = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		left.setAlignmentY(CENTER_ALIGNMENT);
		right.setAlignmentY(CENTER_ALIGNMENT);
		row.add(left);
		row.add(middle);
		row.add(right);

		/*
		 * Fill the left column
		 *
		 * Add filler to shift the hand slots down. Shift * 2 because centering
		 * the column uses the other half at the bottom.
		 */
		left.add(Box.createVerticalStrut(HAND_YSHIFT * 2)); 
		SpriteStore store = SpriteStore.get();
		ItemPanel panel = new ItemPanel("rhand", store.getSprite("data/gui/weapon-slot.png"));
		slotPanels.put("rhand", panel);
		left.add(panel);
		
		panel = new ItemPanel("finger", store.getSprite("data/gui/ring-slot.png"));
		slotPanels.put("finger", panel);
		left.add(panel);
		
		// Fill the middle column
		panel = new ItemPanel("head", store.getSprite("data/gui/helmet-slot.png"));
		slotPanels.put("head", panel);
		middle.add(panel);
		
		panel = new ItemPanel("armor", store.getSprite("data/gui/armor-slot.png"));
		slotPanels.put("armor", panel);
		middle.add(panel);
		
		panel = new ItemPanel("legs", store.getSprite("data/gui/legs-slot.png"));
		slotPanels.put("legs", panel);
		middle.add(panel);
		
		panel = new ItemPanel("feet", store.getSprite("data/gui/boots-slot.png"));
		slotPanels.put("feet", panel);
		middle.add(panel);
	
		/*
		 *  Fill the right column
		 *
		 * Add filler to shift the hand slots down. Shift * 2 because centering
		 * the column uses the other half at the bottom.
		 */
		right.add(Box.createVerticalStrut(HAND_YSHIFT * 2));
		panel = new ItemPanel("lhand", store.getSprite("data/gui/shield-slot.png"));
		slotPanels.put("lhand", panel);
		right.add(panel);
		
		panel = new ItemPanel("cloak", store.getSprite("data/gui/cloak-slot.png"));
		slotPanels.put("cloak", panel);
		right.add(panel);
		
		setContent(row);
	}
	
	/**
	 * Updates the player slot panels.
	 */
	private void refreshContents() {
		// traverse all displayed slots
		for (final Entry<String, ItemPanel> entry : slotPanels.entrySet()) {
			final RPSlot slot = player.getSlot(entry.getKey());

			if (slot == null) {
				continue;
			}

			final ItemPanel entitySlot = entry.getValue();

			if (entitySlot != null) {
				entitySlot.setParent(player);

				final Iterator<RPObject> iter = slot.iterator();

				if (iter.hasNext()) {
					final RPObject object = iter.next();

					IEntity entity = EntityFactory.createEntity(object);

					entitySlot.setEntity(entity);
				} else {
					entitySlot.setEntity(null);
				}
			}
		}

		setTitle(player.getName());
	}

	public void entityChanged(IEntity entity, Object property) {
		if (property == IEntity.PROP_CONTENT) {
			refreshContents();
		}
	}
}
