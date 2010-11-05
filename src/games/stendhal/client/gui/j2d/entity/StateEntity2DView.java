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

//
//

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The 2D view of an animated entity.
 */
abstract class StateEntity2DView extends Entity2DView {
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
	protected void buildAnimations(IEntity entity) {
		buildSprites(entity, sprites);
	}

	/**
	 * Populate named state sprites.
	 * 
	 * @param entity The entity to build sprites for
	 * @param map
	 *            The map to populate. 
	 */
	protected abstract void buildSprites(IEntity entity, final Map<Object, Sprite> map);
	
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
	 * @return The model state.
	 */
	protected abstract Object getState();

	/**
	 * Get the current animated sprite.
	 * 
	 * @return The appropriate sprite for the current state.
	 */
	protected Sprite getStateSprite() {
		final Object state = getState();
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
	protected void buildRepresentation(IEntity entity) {
		buildAnimations(entity);

		setSprite(getStateSprite());
	}

	protected void proceedChangedState() {

		setSprite(getStateSprite());

	}

}
