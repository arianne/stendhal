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

import java.util.List;
import java.util.Map;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a door.
 */
class Door2DView extends StateEntity2DView<Door> {
	/*
	 * The closed state.
	 */
	private static final String STATE_CLOSED = "close";

	/*
	 * The open state.
	 */
	private static final String STATE_OPEN = "open";

	/*
	 * The drawn width.
	 */
	private int width;

	/*
	 * The drawn height.
	 */
	private int height;

	/**
	 * Create a 2D view of a door.
	 */
	public Door2DView() {
		width = IGameScreen.SIZE_UNIT_PIXELS;
		height = IGameScreen.SIZE_UNIT_PIXELS;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(Door entity, final Map<Object, Sprite> map) {
		final String name = entity.getEntityClass();

		final SpriteStore store = SpriteStore.get();

		if (name == null) {
			width = IGameScreen.SIZE_UNIT_PIXELS;
			height = IGameScreen.SIZE_UNIT_PIXELS;

			final Sprite emptySprite = store.getEmptySprite(width, height);

			map.put(STATE_OPEN, emptySprite);
			map.put(STATE_CLOSED, emptySprite);
		} else {
			ZoneInfo info = ZoneInfo.get();
			final Sprite tiles = store.getModifiedSprite(translate(name),
					info.getZoneColor(), info.getColorMethod());

			width = tiles.getWidth();
			height = tiles.getHeight() / 2;

			map.put(STATE_OPEN, store.getTile(tiles, 0, 0, width, height));
			map.put(STATE_CLOSED, store
					.getTile(tiles, 0, height, width, height));
		}

		calculateOffset(entity, width, height);
	}

	/**
	 * Get the current entity state.
	 *
	 * @param entity
	 * @return The current state.
	 */
	@Override
	protected Object getState(Door entity) {
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

		list.add(ActionType.USE.getRepresentation());
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
		return 5000;
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
		return "data/sprites/doors/" + name + ".png";
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		} else if (property == Door.PROP_OPEN) {
			proceedChangedState(entity);
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
		case USE:
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}


	@Override
	public boolean isInteractive() {
		return entity.isUseable();
	}

	@Override
	public StendhalCursor getCursor() {
		if (isInteractive()) {
			return StendhalCursor.ACTIVITY;
		} else {
			return StendhalCursor.PORTAL;
		}
	}

}
