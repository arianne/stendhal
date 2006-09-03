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

import marauroa.common.Log4J;
import marauroa.common.game.*;
import games.stendhal.client.*;
import games.stendhal.common.Direction;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * This class is a special type of GameEntity that has animation, that is it is
 * compound of multiple frames.
 */
public abstract class AnimatedEntity extends Entity {
	private static final Logger logger = Log4J.getLogger(AnimatedEntity.class);

	/** This map contains animation name, frames association */
	protected Map<String, Sprite[]> sprites;

	/** actual animation */
	protected String animation;

	/** actual frame * */
	protected int frame;

	/**
	 * we need to measure time to ahve a coherent frame rendering, that is what
	 * delta is for
	 */
	protected long delta;

	public AnimatedEntity(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
		delta = System.currentTimeMillis();
		frame = 0;
	}

	/** This method fills the sprites map */
	abstract protected void buildAnimations(RPObject object);

	/** This method sets the default animation */
	protected Sprite defaultAnimation() {
		return null;
	}

	/**
	 * Redefined method to load all the animation and set a default frame to be
	 * rendered
	 */
	protected void loadSprite(RPObject object) {
		sprites = new HashMap<String, Sprite[]>();

		buildAnimations(object);
		sprite = defaultAnimation();
	}

	public void onMove(int x, int y, Direction direction, double speed) {
		super.onMove(x, y, direction, speed);

		if (!stopped()) {
			if (dx > 0 && dx * dx >= dy * dy) {
				animation = "move_right";
			} else if (dx < 0 && dx * dx >= dy * dy) {
				animation = "move_left";
			}

			if (dy > 0 && dy * dy >= dx * dx) {
				animation = "move_down";
			} else if (dy < 0 && dy * dy >= dx * dx) {
				animation = "move_up";
			}
		} else {
			int value = direction.get();
			switch (value) {
			case 4:
				animation = "move_left";
				break;
			case 2:
				animation = "move_right";
				break;
			case 1:
				animation = "move_up";
				break;
			case 3:
				animation = "move_down";
				break;
			}
		}
	}

	protected String getAnimation() {
		return animation;
	}

	/** Returns the next Sprite we have to show */
	protected Sprite nextFrame() {
		Sprite[] anim = sprites.get(animation);

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
	public void draw(GameScreen screen) {
		if (System.currentTimeMillis() - delta > 100) {
			delta = System.currentTimeMillis();
			sprite = nextFrame();
		}

		super.draw(screen);
	}
}
