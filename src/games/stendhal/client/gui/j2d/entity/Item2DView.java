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
package games.stendhal.client.gui.j2d.entity;


import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.Item;
import games.stendhal.client.gui.InternalWindow;
import games.stendhal.client.gui.InternalWindow.CloseListener;
import games.stendhal.client.gui.SlotWindow;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.MathHelper;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * The 2D view of an item.
 *
 * @param <T> item type
 */
class Item2DView<T extends Item> extends Entity2DView<T> {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(Item2DView.class);
	/** Default size of the container slot. */
	private static final int DEFAULT_SLOT_SIZE = 8;

	/** Window for showing the slot contents, if any. */
	private volatile SlotWindow slotWindow;
	/** Width of the slot window. */
	private int slotWindowWidth;
	/** height of the slot window. */
	private int slotWindowHeight;

	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(T entity) {
		final SpriteStore store = SpriteStore.get();
		Sprite sprite;
		// Colour items on the ground, but not in bags, corpses etc.
		if (!isContained()) {
			ZoneInfo info = ZoneInfo.get();
			sprite = store.getModifiedSprite(translate(getClassResourcePath()),
					info.getZoneColor(), info.getColorMethod());
		} else {
			sprite = store.getSprite(translate(getClassResourcePath()));
		}

		/*
		 * Items are always 1x1 (they need to fit in entity slots). Extra
		 * columns are animation.
		 */
		final int width = sprite.getWidth();

		if (width > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getAnimatedSprite(sprite, 100);
		} else if (sprite.getHeight() > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getTile(sprite, 0, 0, IGameScreen.SIZE_UNIT_PIXELS,
					IGameScreen.SIZE_UNIT_PIXELS);
			logger.warn("Multi-row item image for: " + getClassResourcePath());
		}

		setSprite(sprite);
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 *
	 * Also, players can only interact with the topmost entity.
	 *
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 7000;
	}

	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param name
	 *            The resource name.
	 *
	 * @return The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/items/" + name + ".png";
	}

	//
	// EntityChangeListener
	//

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 *
	 * @return <code>true</code> if the entity is movable.
	 */
	@Override
	public boolean isMovable() {
		return true;
	}

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.USE);
	}

	@Override
	public boolean onHarmlessAction() {
		return false;
	}

	/**
	 * Set the content inspector for this entity.
	 *
	 * @param inspector
	 *            The inspector.
	 */
	@Override
	public void setInspector(final Inspector inspector) {
		if ((getContent() != null) && (inspector != null)) {
			// Autoinspect containers. They have client visible slots only when
			// carried by the user.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					inspect(inspector);
				}
			});
		}
	}

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case USE:
			/*
			 * Send use action even for released items, if they are in a slot.
			 * Those get released when the slot contents change, and a new view
			 * is created. Users expect to be able to use multiple double clicks
			 * or using a menu created previously for the item.
			 */
			if (!isReleased() || !entity.isOnGround()) {
				at.send(at.fillTargetInfo(entity));
			}
			break;

		default:
			super.onAction(at);
			break;
		}
	}


	/**
	 * Inspect the item. Show the slot contents.
	 *
	 * @param inspector inspector
	 */
	private void inspect(Inspector inspector) {
		RPSlot slot = getContent();
		if (slotWindowWidth == 0) {
			int capacity = getSlotCapacity(slot);
			calculateWindowProportions(capacity);
		}

		boolean addListener = slotWindow == null;
		SlotWindow window = inspector.inspectMe(entity, slot,
				slotWindow, slotWindowWidth, slotWindowHeight);
		slotWindow = window;
		if (window != null) {
			// Don't let the user remove the container windows to keep the UI as
			// clean as possible.
			window.setCloseable(false);
			/*
			 * Register a listener for window closing so that we can
			 * drop the reference to the closed window and let the
			 * garbage collector claim it.
			 */
			if (addListener) {
				window.addCloseListener(new CloseListener() {
					@Override
					public void windowClosed(InternalWindow window) {
						slotWindow = null;
					}
				});
			}
			/*
			 * In case the view got released while the window was created and
			 * added, and before the main thread was aware that there's a window
			 * to be closed, close it now. (onAction is called from the event
			 * dispatch thread).
			 */
			if (isReleased()) {
				window.close();
			}
		}
	}

	/**
	 * Get the actual size of the container slot of a container item.
	 *
	 * @param slot container slot
	 * @return size of the container slot
	 */
	private int getSlotCapacity(RPSlot slot) {
		RPObject obj = entity.getRPObject();
		if (obj.has("slot_size")) {
			return MathHelper.parseIntDefault(obj.get("slot_size"), DEFAULT_SLOT_SIZE);
		}
		// Fall back to default slot size (should not happen)
		logger.warn("Container is missing slot size: " + obj);
		return slot.getCapacity();
	}

	/**
	 * Get the content slot.
	 *
	 * @return Content slot or <code>null</code> if the item has none or it's
	 * not accessible.
	 */
	private RPSlot getContent() {
		return entity.getContent();
	}

	/**
	 * Find out dimensions for a somewhat square slot window.
	 *
	 * @param slots number of slots in the window
	 */
	private void calculateWindowProportions(final int slots) {
		int width = (int) Math.sqrt(slots);

		while (slots % width != 0) {
			width--;
			if (width <= 0) {
				logger.error("Failed to decide dimensions for slot window. slots = " + slots);

				width = 1;
			}
		}
		slotWindowWidth = width;
		slotWindowHeight = slots / width;
	}

	@Override
	public void release() {
		final SlotWindow window = slotWindow;
		if (window != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					window.close();
				}
			});
		}

		super.release();
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.NORMAL;
	}
}
