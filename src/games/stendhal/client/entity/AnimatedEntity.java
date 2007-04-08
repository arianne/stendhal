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
import games.stendhal.common.Direction;

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

	/**
	 * The current direction.
	 * This (and other direction stuff) should be moved to sub-class.
	 */
	private Direction	direction;

	/** This method fills the sprites map 
	 * @param object
	 */
	protected void buildAnimations(RPObject object) {
		((AnimatedEntity2DView) view).buildAnimations(sprites, object);
	}

	/** This method sets the default animation */
	protected Sprite defaultAnimation() {
		return null;
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


	public Direction getDirection() {
		return direction;
	}


	@Override
	public void onMove(final int x, final int y, final Direction direction, final double speed) {
		super.onMove(x, y, direction, speed);

		this.direction = direction;

		adjustAnimation(direction);
	}

	protected void adjustAnimation(final Direction direction) {
	  
		switch (direction) {
			case LEFT:
				animation = "move_left";
				break;
			case RIGHT:
				animation = "move_right";
				break;
			case UP:
				animation = "move_up";
				break;
			case DOWN:
				animation = "move_down";
				break;
		}
	}

	/**
	 * Get the current entity state.
	 */
	public String getState() {
		return getAnimation();
	}

	protected String getAnimation() {
		return animation;
	}

	/**
	 * Temp for AnimatedEntity2DView (till it gets full impl)
	 */
	public Sprite [] getSprites(final String animation) {
		return sprites.get(animation);
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new AnimatedEntity2DView(this);
	}
}
