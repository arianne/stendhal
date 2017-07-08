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


import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a grain field.
 *
 * @param <T> grower type
 */
class GrainField2DView<T extends GrainField> extends StateEntity2DView<T> {
	/**
	 * Log4J.
	 */
	private static final Logger LOGGER = Logger.getLogger(GrainField2DView.class);

	/**
	 * Create a 2D view of a grain field.
	 */
	public GrainField2DView() {
		super();
		setSpriteAlignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param entity the entity to build sprites for
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(T entity, final Map<Object, Sprite> map) {
		int width = getWidth();
		String clazz = entity.getEntityClass();

		if (clazz == null) {
			LOGGER.warn("No entity class set");
			clazz = "grain_field";
		}

		final SpriteStore store = SpriteStore.get();
		ZoneInfo info = ZoneInfo.get();
		final Sprite tiles = store.getModifiedSprite(translate(clazz.replace(" ", "_")),
				info.getZoneColor(), info.getColorMethod());

		int states = entity.getMaximumRipeness() + 1;

		final int tileSetHeight = tiles.getHeight();
		final int imageHeight = tileSetHeight / states;
		if (tileSetHeight % states != 0) {
			LOGGER.warn("Inconsistent image height in "
					+ translate(clazz.replace(" ", "_")) + ": image height "
					+ tileSetHeight + " with " + states + " states.");
		}

		int i = 0;
		for (int y = 0; y < tileSetHeight; y += imageHeight) {
			map.put(Integer.valueOf(i++), store.getTile(tiles, 0, y, width,
					imageHeight));
		}

		calculateOffset(entity, width, imageHeight);
	}

	/**
	 * Get the current entity state.
	 *
	 * @param entity checked entity
	 * @return The current state.
	 */
	@Override
	protected Object getState(T entity) {
		return Integer.valueOf(entity.getRipeness());
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
		if (!entity.getRPObject().has("menu")) {
			list.add(ActionType.HARVEST.getRepresentation());
		}

		super.buildActions(list);
	}

	/**
	 * Get the height.
	 *
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return (int) (entity.getHeight() * IGameScreen.SIZE_UNIT_PIXELS);
	}

	/**
	 * Get the width.
	 *
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return (int) (entity.getWidth() * IGameScreen.SIZE_UNIT_PIXELS);
	}

	@Override
	public Rectangle getArea() {
		return new Rectangle(getX() + getXOffset(), getY(),
				getWidth(), getHeight());
	}

	@Override
	protected Rectangle getDrawingArea() {
		/*
		 * The area of the entire sprite can be larger than the entity area
		 * returned by getArea, so we need to provide the info for Entity2DView
		 * here.
		 */
		return new Rectangle(getX() + getXOffset(), getY() + getYOffset(),
				getWidth(), getHeight() - getYOffset());
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
		return 3000;
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		} else if (property == GrainField.PROP_RIPENESS) {
			proceedChangedState(entity);
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.HARVEST);
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
		case HARVEST:
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.HARVEST;
	}

}
