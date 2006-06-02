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

import games.stendhal.client.GameObjects;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class SheepFood extends AnimatedEntity {
	private int amount;

	public SheepFood(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
	}

	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();

		sprites.put("0", store.getAnimatedSprite(translate(object.get("type")),
				0, 1, 1, 1));
		sprites.put("1", store.getAnimatedSprite(translate(object.get("type")),
				1, 1, 1, 1));
		sprites.put("2", store.getAnimatedSprite(translate(object.get("type")),
				2, 1, 1, 1));
		sprites.put("3", store.getAnimatedSprite(translate(object.get("type")),
				3, 1, 1, 1));
		sprites.put("4", store.getAnimatedSprite(translate(object.get("type")),
				4, 1, 1, 1));
		sprites.put("5", store.getAnimatedSprite(translate(object.get("type")),
				5, 1, 1, 1));
	}

	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
	}

	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("amount")) {
			int oldAmount = amount;
			animation = diff.get("amount");
			amount = diff.getInt("amount");

			// TODO this causes problems because of unidentified content refresh
			// events (e.g. synchronizing)
			if (amount > oldAmount)
				playSound("fruit-regrow", 10, 25);
		} else if (base.has("amount")) {
			animation = base.get("amount");
			amount = base.getInt("amount");
		}
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public int compare(Entity entity) {
		if (entity instanceof Item) {
			return -1;
		}

		return -1;
	}
}
