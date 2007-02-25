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

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Blood extends AnimatedEntity {

	public Blood( RPObject base)
			throws AttributeNotFoundException {
		super( base);
	}

	protected void buildAnimations(RPObject base) {
		SpriteStore store = SpriteStore.get();

		sprites.put("0", store.getAnimatedSprite(
				"data/sprites/combat/blood_red.png", 0, 1, 1, 1));
		sprites.put("1", store.getAnimatedSprite(
				"data/sprites/combat/blood_red.png", 1, 1, 1, 1));
		sprites.put("2", store.getAnimatedSprite(
				"data/sprites/combat/blood_red.png", 2, 1, 1, 1));
		sprites.put("3", store.getAnimatedSprite(
				"data/sprites/combat/blood_red.png", 3, 1, 1, 1));
	}

	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
	}

	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("class")) {
			animation = diff.get("class");
		}
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public int getZIndex() {
		return 2000;
	}

}
