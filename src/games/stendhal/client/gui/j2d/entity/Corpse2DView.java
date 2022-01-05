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


import java.awt.Graphics2D;
import java.util.List;

import javax.swing.SwingUtilities;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.ContentChangeListener;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.InternalWindow;
import games.stendhal.client.gui.InternalWindow.CloseListener;
import games.stendhal.client.gui.SlotWindow;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import marauroa.common.game.RPSlot;

/**
 * The 2D view of a corpse.
 *
 * @param <T> corpse type
 */
class Corpse2DView<T extends Corpse> extends Entity2DView<T> {

	/**
	 * The corpse height.
	 */
	private int height;

	/**
	 * The corpse width.
	 */
	private int width;

	/**
	 * The slot content inspector.
	 */
	private Inspector inspector;

	/**
	 * The current content inspector.
	 */
	private volatile SlotWindow slotWindow;

	/**
	 * Has the corpse been opened once on an auto raise?
	 */
	private boolean autoOpenedAlready = false;

	/**
	 * Create a 2D view of an entity.
	 */
	public Corpse2DView() {
		height = IGameScreen.SIZE_UNIT_PIXELS;
		width = IGameScreen.SIZE_UNIT_PIXELS;
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.INSPECT.getRepresentation());

		super.buildActions(list);
	}

	/**
	 * Build the visual representation of this entity.
	 *
	 * @param entity Entity to display
	 */
	@Override
	protected void buildRepresentation(T entity) {
		ZoneInfo info = ZoneInfo.get();
		boolean showBlood = WtWindowManager.getInstance().getPropertyBoolean("gamescreen.blood", true);
		String imageName;
		if (showBlood) {
			imageName = entity.getRPObject().get("image");
		} else {
			imageName = entity.getRPObject().get("harmless_image");
		}
		Sprite sprite = SpriteStore.get().getModifiedSprite(translate("corpse/"  + imageName),
				info.getZoneColor(), info.getColorMethod());

		width = sprite.getWidth();
		height = sprite.getHeight();

		setSprite(sprite);

		calculateOffset(entity, width, height);
	}

	/**
	 * Get the height.
	 *
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width.
	 *
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return width;
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
		return 5500;
	}

	/**
	 * Set the content inspector for this entity.
	 *
	 * @param inspector
	 *            The inspector.
	 */
	@Override
	public void setInspector(final Inspector inspector) {
		this.inspector = inspector;
	}

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
		onAction(ActionType.INSPECT);
	}

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		if (isReleased()) {
			return;
		}
		switch (at) {
		case INSPECT:
			boolean addListener = slotWindow == null;
			RPSlot content = entity.getContent();

			int content_row = 2;
			final int content_col = 2;
			if (content.size() > 4) {
				content_row = 3;
			}

			slotWindow = inspector.inspectMe(entity, content, slotWindow, content_row, content_col);
			SlotWindow window = slotWindow;
			if (window != null) {
				window.setTitle(entity.getTitle());
				window.setMinimizable(false);
				prepareInspectAutoClose(window, entity, content);
			}
			/*
			 * Register a listener for window closing so that we can
			 * drop the reference to the closed window and let the
			 * garbage collector claim it.
			 */
			if (addListener && (window != null)) {
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
				if (window != null) {
					window.close();
				}
			}
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	/**
	 * Attach a listener to the inspector window, so that the window will be
	 * closed when all of the contents of the inspected slot are removed.
	 *
	 * @param window inspector window
	 * @param entity inspected entity
	 * @param slot inspected slot
	 */
	private void prepareInspectAutoClose(final SlotWindow window, final IEntity entity, final RPSlot slot) {
		entity.addContentChangeListener(new ContentChangeListener() {
			@Override
			public void contentAdded(RPSlot added) {
				// Unused
			}

			@Override
			public void contentRemoved(RPSlot removed) {
				if (slot.size() == removed.size()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							window.close();
						}
					});
				}
			}
		});
	}

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 */
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
		StendhalCursor cursor = super.getCursor();
		Corpse corpse = entity;

		// server override?
		if ((cursor != StendhalCursor.UNKNOWN) || (corpse == null)) {
			return cursor;
		}

		// empty?
		if (corpse.getContent().size() == 0) {
			return StendhalCursor.EMPTY_BAG;
		}

		// owner
		if ((corpse.getCorpseOwner() == null) || corpse.getCorpseOwner().equals(User.getCharacterName())) {
			return StendhalCursor.BAG;
		}
		if (User.isGroupSharingLoot() && User.isPlayerInGroup(corpse.getCorpseOwner())) {
			return StendhalCursor.BAG;
		}
		return StendhalCursor.LOCKED_BAG;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.gui.j2d.entity.Entity2DView#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g2d) {
		super.draw(g2d);
		autoRaiseWindowIfDesired();
	}

	/**
	 * Immediately opens the corpse window if the player deserves the kill
	 * (is corpse owner) and has that setting specified.
	 */
	private void autoRaiseWindowIfDesired() {
		// inspector is null for entities in the drag layer. Those should not
		// be auto inspected anyway
		if (!autoOpenedAlready && inspector != null) {
			autoOpenedAlready = true;
			boolean autoRaiseCorpse = WtWindowManager.getInstance().getPropertyBoolean("gamescreen.autoinspectcorpses", true);
			if (autoRaiseCorpse
					&& (entity.getCorpseOwner() != null)
					&& entity.getCorpseOwner().equals(User.getCharacterName())
					&& !entity.isEmpty()) {
				/*
				 * We are in mid-draw of the screen. Defer auto inspect to
				 * avoid messing with the component layout while drawing.
				 * Fixes flicker in certain situations (bug #3302772).
				 */
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						onAction(ActionType.INSPECT);
					}
				});
			}
		}
	}
}
