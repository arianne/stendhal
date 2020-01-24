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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.ContentChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.sprite.SpriteStore;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.game.RPSlot;

/**
 * Window for showing the equipment the player is wearing.
 */
class Character extends InternalManagedWindow implements ContentChangeListener,
Inspectable {
	/**
	 * serial version uid.
	 */
	private static final long serialVersionUID = -5585214190674481472L;

	/** Padding between the ItemPanels. */
	private static final int PADDING = 1;
	/** The pixel amount the hand slots should be below the armor slot. */
	private static final int HAND_YSHIFT = 10;
	private static final Logger logger = Logger.getLogger(Character.class);

	/** ItemPanels searchable by the respective slot name. */
	private final Map<String, ItemPanel> slotPanels = new HashMap<String, ItemPanel>();
	private User player;

	private JComponent specialSlots;

	private static final List<FeatureChangeListener> featureChangeListeners = new ArrayList<>();

	private static FeatureEnabledItemPanel pouch;

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
	 * Sets the player entity. It is safe to call this method outside the event
	 * dispatch thread.
	 *
	 * @param userEntity new user
	 */
	public void setPlayer(final User userEntity) {
		player = userEntity;
		userEntity.addContentChangeListener(this);

		//final RPObject obj = userEntity.getRPObject();

		// Compatibility. Show additional slots only if the user has those.
		// This can be removed after a couple of releases (and specialSlots
		// field moved to createLayout()).
		/*
		if (obj.hasSlot("belt")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					specialSlots.setVisible(true);
				}
			});
		}
		*/

		refreshContents();
	}

	/**
	 * Create the content layout and add the ItemPanels.
	 */
	private void createLayout() {
		// Layout containers
		JComponent content = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, PADDING);
		JComponent left = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		JComponent middle = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		JComponent right = SBoxLayout.createContainer(SBoxLayout.VERTICAL, PADDING);
		left.setAlignmentY(CENTER_ALIGNMENT);
		right.setAlignmentY(CENTER_ALIGNMENT);
		row.add(left);
		row.add(middle);
		row.add(right);
		content.add(row);

		Class<? extends IEntity> itemClass = EntityMap.getClass("item", null, null);
		SpriteStore store = SpriteStore.get();

		/*
		 * Fill the left column
		 *
		 * Add filler to shift the hand slots down. Shift * 2 because centering
		 * the column uses the other half at the bottom.
		 */
		left.add(Box.createVerticalStrut(HAND_YSHIFT * 2));

		ItemPanel panel = createItemPanel(itemClass, store, "rhand", "data/gui/slot-weapon.png");
		left.add(panel);
		panel = createItemPanel(itemClass, store, "finger", "data/gui/slot-ring.png");
		left.add(panel);

		// Fill the middle column
		panel = createItemPanel(itemClass, store, "head", "data/gui/slot-helmet.png");
		middle.add(panel);
		panel = createItemPanel(itemClass, store, "armor", "data/gui/slot-armor.png");
		middle.add(panel);
		panel = createItemPanel(itemClass, store, "legs", "data/gui/slot-legs.png");
		middle.add(panel);
		panel = createItemPanel(itemClass, store, "feet", "data/gui/slot-boots.png");
		middle.add(panel);

		/*
		 *  Fill the right column
		 *
		 * Add filler to shift the hand slots down. Shift * 2 because centering
		 * the column uses the other half at the bottom.
		 */
		right.add(Box.createVerticalStrut(HAND_YSHIFT * 2));
		panel = createItemPanel(itemClass, store, "lhand", "data/gui/slot-shield.png");
		right.add(panel);
		panel = createItemPanel(itemClass, store, "cloak", "data/gui/slot-cloak.png");

		right.add(panel);

		// Bag, keyring, etc
		specialSlots = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, PADDING);
		specialSlots.setAlignmentX(CENTER_ALIGNMENT);
		// Compatibility. See the note at setPlayer().
		//specialSlots.setVisible(false);
		content.add(specialSlots);

		pouch = new FeatureEnabledItemPanel("pouch", SpriteStore.get().getSprite("data/gui/slot-pouch.png"));
		slotPanels.put("pouch", pouch);
		pouch.setAcceptedTypes(itemClass);
		specialSlots.add(pouch);
		featureChangeListeners.add(pouch);

		/*
		panel = createItemPanel(itemClass, store, "belt", "data/gui/slot-key.png");
		specialSlots.add(panel);
		*/

		setContent(content);
	}

	/**
	 * Create an item panel to be placed to the character window.
	 *
	 * @param itemClass acceptable drops to the slot
	 * @param store sprite store
	 * @param id slot identifier
	 * @param image empty slot image
	 *
	 * @return item panel
	 */
	private ItemPanel createItemPanel(Class<? extends IEntity> itemClass, SpriteStore store, String id, String image) {
		ItemPanel panel = new ItemPanel(id, store.getSprite(image));
		slotPanels.put(id, panel);
		panel.setAcceptedTypes(itemClass);

		return panel;
	}

	/**
	 * Updates the player slot panels.
	 */
	private void refreshContents() {
		for (final Entry<String, ItemPanel> entry : slotPanels.entrySet()) {
			final ItemPanel entitySlot = entry.getValue();

			if (entitySlot != null) {
				// Set the parent entity for all slots, even if they are not
				// visible. They may become visible without zone changes
				entitySlot.setParent(player);

				final RPSlot slot = player.getSlot(entry.getKey());
				if (slot == null) {
					continue;
				}

				final Iterator<RPObject> iter = slot.iterator();

				if (iter.hasNext()) {
					final RPObject object = iter.next();

					IEntity entity = GameObjects.getInstance().get(object);

					entitySlot.setEntity(entity);
				} else {
					entitySlot.setEntity(null);
				}
			}
		}

		/*
		 * Refresh gets called from outside the EDT.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setTitle(player.getName());
			}
		});
	}

	@Override
	public void contentAdded(RPSlot added) {
		ItemPanel panel = slotPanels.get(added.getName());
		if (panel == null) {
			// Not a slot we are interested in
			return;
		}

		/*
		String slotName = added.getName();
		if (("belt".equals(slotName) || "back".equals(slotName)) && !player.getRPObject().hasSlot(slotName)) {
			// One of the new slots was added to the player. Set them visible.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					specialSlots.setVisible(true);
				}
			});
		}
		*/

		for (RPObject obj : added) {
			ID id = obj.getID();
			IEntity entity = panel.getEntity();
			if (entity != null && id.equals(entity.getRPObject().getID())) {
				// Changed rather than added.
				return;
			}
			// Actually added, fetch the corresponding entity
			entity = GameObjects.getInstance().get(obj);
			if (entity == null) {
				logger.error("Unable to find entity for: " + obj,
						new Throwable("here"));
				return;
			}

			panel.setEntity(entity);
		}
	}

	@Override
	public void contentRemoved(RPSlot removed) {
		ItemPanel panel = slotPanels.get(removed.getName());
		if (panel == null) {
			// Not a slot we are interested in
			return;
		}
		for (RPObject obj : removed) {
			ID id = obj.getID();
			IEntity entity = panel.getEntity();
			if (entity != null && id.equals(entity.getRPObject().getID())) {
				if (obj.size() == 1) {
					// The object was removed
					panel.setEntity(null);
					continue;
				}
			} else {
				logger.error("Tried removing wrong object from a panel. "
						+ "removing: " + obj + " , but panel contains: "
						+ panel.getEntity(), new Throwable());
			}
		}
	}

	@Override
	public void setInspector(Inspector inspector) {
		for (ItemPanel panel : slotPanels.values()) {
			panel.setInspector(inspector);
		}
	}

	/**
	 * Retrieves all the listeners for this panel.
	 *
	 * @return
	 * 		<code>List<FeatureChangeListener></code>
	 */
	public List<FeatureChangeListener> getFeatureChangeListeners() {
		return featureChangeListeners;
	}
}
