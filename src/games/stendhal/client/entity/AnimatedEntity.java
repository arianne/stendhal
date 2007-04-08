/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.Sprite;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;

/**
 * This class is a special type of GameEntity that has animation, that is it is
 * compound of multiple frames.
 */
public abstract class AnimatedEntity extends Entity {
	/** This map contains animation name, frames association */
	protected Map < String, Sprite[] > sprites;

	/** actual animation */
	protected String animation;

	/** This method fills the sprites map 
	 * @param object
	 */
	protected void buildAnimations(RPObject object) {
		((AnimatedEntity2DView) view).buildAnimations(sprites, object);
//		view.buildRepresentation(sprites, object);
	}

	/** This method sets the default animation */
	protected Sprite defaultAnimation() {
		return ((AnimatedEntity2DView) view).getDefaultSprite();
	}

	/**
	 * Redefined method to load all the animation and set a default frame to be
	 * rendered
	 */
	@Override
	protected void loadSprite(final RPObject object) {
		sprites = new HashMap < String, Sprite[] >();

		buildAnimations(object);
		sprite = defaultAnimation();
	}


	/**
	 * Get the current entity state.
	 */
	public String getState() {
		return animation;
	}


	/**
	 * Temp for AnimatedEntity2DView (till it gets full impl)
	 */
	public Sprite [] getSprites(final String animation) {
		return sprites.get(animation);
	}
}
