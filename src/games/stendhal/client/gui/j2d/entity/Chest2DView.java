/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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


import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.InternalWindow;
import games.stendhal.client.gui.InternalWindow.CloseListener;
import games.stendhal.client.gui.SlotWindow;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a chest.
 */
class Chest2DView extends StateEntity2DView<Chest> {
	/*
	 * The closed state.
	 */
	private static final String STATE_CLOSED = "close";

	/*
	 * The open state.
	 */
	private static final String STATE_OPEN = "open";

	/**
	 * The chest model open value changed.
	 */
	private volatile boolean openChanged;

	/**
	 * The slot content inspector.
	 */
	private Inspector inspector;

	/**
	 * Whether the user requested to open this chest.
	 */
	private boolean requestOpen;

	/**
	 * The current content inspector.
	 */
	private SlotWindow slotWindow;

	/**
	 * Create a 2D view of a chest.
	 */
	public Chest2DView() {
		openChanged = false;
		requestOpen = false;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param entity entity to build sprites for
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(Chest entity, final Map<Object, Sprite> map) {
		final SpriteStore store = SpriteStore.get();
		ZoneInfo info = ZoneInfo.get();
		final Sprite tiles = store.getModifiedSprite(translate(entity.getType()),
				info.getZoneColor(), info.getColorMethod());

		map.put(STATE_CLOSED, store.getTile(tiles, 0, 0,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS));
		map.put(STATE_OPEN, store.getTile(tiles, 0,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS));
	}

	/**
	 * Get the current entity state.
	 *
	 * @param entity
	 * @return The current state.
	 */
	@Override
	protected Object getState(Chest entity) {
		if (entity.isOpen()) {
			return STATE_OPEN;
		} else {
			return STATE_CLOSED;
		}
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
		super.buildActions(list);

		Chest chest = entity;
		if (chest != null && chest.isOpen()) {
			list.add(ActionType.INSPECT.getRepresentation());
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());
		}
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
		return 5000;
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

	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (openChanged) {
			openChanged = false;
			if (entity.isOpen()) {
				// we're wanted to open this?
				if (requestOpen) {
					/*
					 * The component hierarchy of the game screen should not
					 * be modified in middle of the draw, so we push the
					 * operation to the end of the queue.
					 */
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							showWindow();
						}
					});
				}
			} else {
				if (slotWindow != null) {
					slotWindow.close();
				}
			}

			requestOpen = false;
		}
	}

	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == Chest.PROP_OPEN) {
			proceedChangedState(entity);
			openChanged = true;
		}
	}

	//
	// EntityView
	//

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
			showWindow();
			break;

		case OPEN:
			if (!entity.isOpen()) {
				// If it was closed, open it and inspect it...
				requestOpen = true;
			}

			at.send(at.fillTargetInfo(entity));
			break;

		case CLOSE:

			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public void onAction() {
		if (entity.isOpen()) {
			this.onAction(ActionType.INSPECT);
		} else {
			this.onAction(ActionType.OPEN);
		}
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
		// TODO: use empty detection like in Corpse2DView, but not for bank chests
		//because they are always empty when closed
		return StendhalCursor.BAG;
	}

	/**
	 * Show the content window.
	 */
	private void showWindow() {
		boolean addListener = slotWindow == null;
		slotWindow = inspector.inspectMe(entity, entity.getContent(),
				slotWindow, 5, 6);
		/*
		 * Register a listener for window closing so that we can
		 * drop the reference to the closed window and let the
		 * garbage collector claim it.
		 */
		if (addListener && (slotWindow != null)) {
			slotWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClosed(InternalWindow window) {
					slotWindow = null;
				}
			});
		}
	}
}
