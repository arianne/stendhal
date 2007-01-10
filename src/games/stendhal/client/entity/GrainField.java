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
import games.stendhal.client.StendhalClient;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class GrainField extends AnimatedEntity {

	public GrainField(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
	}

	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();

		// compatibility to server <= 0.56
		int maxRipeness = 5;
		int width = 1;
		int height = 2;

		// get sprite info
		if (object.has("max_ripeness")) {
			maxRipeness = object.getInt("max_ripeness");
		}
		if (object.has("width")) {
			width = object.getInt("width");
		}
		if (object.has("height")) {
			height = object.getInt("height");
		}

		// load sprites
		for (int i = 0; i <= maxRipeness; i++) {
			sprites.put(Integer.toString(i), store.getAnimatedSprite(translate(object.get("type")),
					i, 1, width, height));
		}
	}

	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("ripeness")) {
			animation = diff.get("ripeness");

		} else if (base.has("ripeness")) {
			animation = base.get("ripeness");
		}
	}

	//
	
	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}
	
	public String defaultAction() {
		return "Harvest";
	}

	public String[] offeredActions() {
		String[] list = { "Look", "Harvest" };
		return list;
	}

	public void onAction(StendhalClient client, String action, String... params) {
		super.onAction(client, action, params);
		if (action.equals("Harvest")) {
			RPAction rpaction = new RPAction();
			rpaction.put("type", "use");
			int id = getID().getObjectID();
			rpaction.put("target", id);
			client.send(rpaction);
		}
	}

	@Override
	public int getZIndex() {
		return 3000;
	}
}
