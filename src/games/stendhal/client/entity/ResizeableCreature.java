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
	double width = 1.5;
	double height = 1.0f;

	public ResizeableCreature(RPObject object) throws AttributeNotFoundException {
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
		if (diff.has("width")) {
			width = diff.getDouble("width");
		} else if (base.has("width")) {
			width = base.getDouble("width");
		}
		if (diff.has("height")) {
			width = diff.getDouble("height");
		} else if (base.has("height")) {
			width = base.getDouble("height");
		}
	    super.onChangedAdded(base, diff);
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
