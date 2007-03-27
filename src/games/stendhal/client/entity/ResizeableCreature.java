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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/** A Creature which a server adjustable size */
public class ResizeableCreature extends Creature {
	private double width = 1.5;
	private double height = 1.0f;
	private String metamorphosis = null;

	/**
	 * Creates a new resizeable creature
	 *
	 * @param object RPObject
	 */
	public ResizeableCreature(RPObject object) {
		super(object);
	}

	@Override
	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();
		Sprite creature = loadAnimationSprite(object);

		sprites.put("move_up", store.getAnimatedSprite(creature, 0, 4, width, height));
		sprites.put("move_right", store.getAnimatedSprite(creature, 1, 4, width, height));
		sprites.put("move_down", store.getAnimatedSprite(creature, 2, 4, width, height));
		sprites.put("move_left", store.getAnimatedSprite(creature, 3, 4, width, height));

		sprites.get("move_up")[3] = sprites.get("move_up")[1];
		sprites.get("move_right")[3] = sprites.get("move_right")[1];
		sprites.get("move_down")[3] = sprites.get("move_down")[1];
		sprites.get("move_left")[3] = sprites.get("move_left")[1];
	}
	
	

	@Override
    public void onChangedAdded(RPObject base, RPObject diff) throws AttributeNotFoundException {
	    super.onChangedAdded(base, diff);
	    boolean rebuildAnimations = false;
		if (diff.has("width")) {
			width = diff.getDouble("width");
			rebuildAnimations = true;
		} else if (base.has("width")) {
			width = base.getDouble("width");
			rebuildAnimations = true;
		}
		if (diff.has("height")) {
			width = diff.getDouble("height");
			rebuildAnimations = true;
		} else if (base.has("height")) {
			width = base.getDouble("height");
			rebuildAnimations = true;
		}
		if (diff.has("metamorphosis")) {
			metamorphosis = diff.get("metamorphosis");
			rebuildAnimations = true;
		} else if (base.has("metamorphosis")) {
			metamorphosis = base.get("metamorphosis");
			rebuildAnimations = true;
		}
		
		if (rebuildAnimations) {
			buildAnimations(base);
		}
    }

	@Override
	public void onChangedRemoved(RPObject base, RPObject diff) {
		super.onChangedRemoved(base, diff);
		if (diff.has("metamorphosis")) {
			metamorphosis = null;
			buildAnimations(base);
		}
	}

	@Override
	protected Sprite loadAnimationSprite(RPObject object) {
		if (metamorphosis == null) {
			return super.loadAnimationSprite(object);
		} else {
			SpriteStore store = SpriteStore.get();
			sprite = store.getSprite("data/sprites/monsters/" + metamorphosis + ".png");
			return sprite;
		}
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "move_up";
		return sprites.get("move_up")[0];
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, width, height);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, width, height);
	}
}
