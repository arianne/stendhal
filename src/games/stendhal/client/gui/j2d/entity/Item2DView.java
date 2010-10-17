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


import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import org.apache.log4j.Logger;

/**
 * The 2D view of an item.
 */
class Item2DView extends Entity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(Item2DView.class);


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(IEntity entity) {
		final SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate(getClassResourcePath()));

		/*
		 * Items are always 1x1 (they need to fit in entity slots). Extra
		 * columns are animation. Extra rows are ignored.
		 */
		final int width = sprite.getWidth();

		if (width > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getAnimatedSprite(sprite, 0, 0, width
					/ IGameScreen.SIZE_UNIT_PIXELS,
					IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
					100);
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

	/**
	 * An entity was changed.
	 * 
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

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
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case USE:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.NORMAL;
	}

}
