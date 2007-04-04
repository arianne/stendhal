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

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.common.Direction;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * This class is a special type of GameEntity that has animation, that is it is
 * compound of multiple frames.
 */
public abstract class AnimatedEntity extends Entity {

	private static final Logger logger = Log4J.getLogger(AnimatedEntity.class);

	/** This map contains animation name, frames association */
	protected Map < String, Sprite[] > sprites;

	/** actual animation */
	protected String animation;

	/** actual frame * */
	protected int frame;

	/**
	 * we need to measure time to ahve a coherent frame rendering, that is what
	 * delta is for
	 */
	protected long delta;

	public AnimatedEntity(final RPObject object) throws AttributeNotFoundException {
		super(object);
		delta = System.currentTimeMillis();
		frame = 0;
	}
	public AnimatedEntity() {
		super();
		delta = System.currentTimeMillis();
		frame = 0;
	}

	/** This method fills the sprites map 
	 * @param object
	 */
	protected abstract void buildAnimations(RPObject object);

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

	@Override
	public void onMove(final int x, final int y, final Direction direction, final double speed) {
		super.onMove(x, y, direction, speed);

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

	protected String getAnimation() {
		return animation;
	}

	/** Returns the next Sprite we have to show */
	private  Sprite nextFrame() {
		Sprite[] anim = sprites.get(getAnimation());

		if (anim == null) {
			logger.fatal(this.getClass().getName() + ": sprites.get() returned null for " + animation);
			return SpriteStore.get().getSprite("data/sprites/failsafe.png");
		}

		if (frame == anim.length) {
			frame = 0;
		}

		Sprite sprite = anim[frame];

		if (!stopped()) {
			frame++;
		}

		return sprite;
	}

	/** Draws this entity in the screen */
	@Override
	public void draw(final GameScreen screen) {
		if (System.currentTimeMillis() - delta > 100) {
			delta = System.currentTimeMillis();
			sprite = nextFrame();
		}

		super.draw(screen);
	}
}
