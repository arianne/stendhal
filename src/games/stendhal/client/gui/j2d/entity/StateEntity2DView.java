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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

//
//

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of an animated entity.
 *
 * @param <T> entity type
 */
abstract class StateEntity2DView<T extends IEntity> extends Entity2DView<T> {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger
			.getLogger(StateEntity2DView.class);

	/**
	 * Map of named sprites.
	 */
	protected Map<Object, Sprite> sprites = new HashMap<Object, Sprite>();

	//
	// StateEntity2DView
	//

	/**
	 * Build animations.
	 *
	 * @param entity the entity to build animations for
	 */
	private void buildAnimations(T entity) {
		buildSprites(entity, sprites);
	}

	/**
	 * Populate named state sprites.
	 *
	 * @param entity The entity to build sprites for
	 * @param map
	 *            The map to populate.
	 */
	protected abstract void buildSprites(T entity, final Map<Object, Sprite> map);

	/**
	 * Get a keyed state sprite.
	 *
	 * @param state
	 *            The state.
	 *
	 * @return The appropriate sprite for the given state.
	 */
	protected Sprite getSprite(final Object state) {
		return sprites.get(state);
	}

	/**
	 * Get the current model state.
	 *
	 * @param entity
	 * @return The model state.
	 */
	protected abstract Object getState(T entity);

	/**
	 * Get the current animated sprite.
	 *
	 * @param entity
	 * @return The appropriate sprite for the current state.
	 */
	private Sprite getStateSprite(T entity) {
		final Object state = getState(entity);
		final Sprite sprite = getSprite(state);

		if (sprite == null) {
			logger.debug("No sprite found for: " + state);
			return SpriteStore.get().getFailsafe();
		}

		return sprite;
	}

	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity. This builds all the
	 * animation sprites and sets the default frame.
	 */
	@Override
	protected void buildRepresentation(T entity) {
		buildAnimations(entity);

		setSprite(getStateSprite(entity));
	}

	/**
	 * Update sprite state of the entity.
	 *
	 * @param entity
	 */
	protected void proceedChangedState(T entity) {
		setSprite(getStateSprite(entity));
	}
}
