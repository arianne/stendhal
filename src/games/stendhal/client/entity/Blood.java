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
import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class Blood extends AnimatedEntity {

	
	

	@Override
	protected void buildAnimations(final RPObject base) {
		SpriteStore store = SpriteStore.get();

		sprites.put("0", store.getAnimatedSprite("data/sprites/combat/blood_red.png", 0, 1, 1, 1));
		sprites.put("1", store.getAnimatedSprite("data/sprites/combat/blood_red.png", 1, 1, 1, 1));
		sprites.put("2", store.getAnimatedSprite("data/sprites/combat/blood_red.png", 2, 1, 1, 1));
		sprites.put("3", store.getAnimatedSprite("data/sprites/combat/blood_red.png", 3, 1, 1, 1));
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
	}

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("class")) {
			animation = diff.get("class");
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public int getZIndex() {
		return 2000;
	}

}
