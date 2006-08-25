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
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class Item extends PassiveEntity {

	public Item(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
	}

	@Override
	protected void loadSprite(RPObject object) {
		SpriteStore store = SpriteStore.get();
		String name = null;

		if (object.has("subclass")) {
			name = object.get("class") + "/" + object.get("subclass");
		} else {
			name = object.get("class");
		}

		sprite = store.getSprite("data/sprites/items/" + name + ".png");
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
	public String defaultAction() {
		return "Use";
	}

	@Override
	public String[] offeredActions() {
		java.util.List<String> list = new ArrayList<String>();
		list.add("Use");
		list.add("Look");
		if (client.isAdmin()) {
			list.add("(*)Inspect");
			list.add("(*)Destroy");
			list.add("(*)Alter");
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Use")) {
			RPAction rpaction = new RPAction();
			rpaction.put("type", "use");
			int id = getID().getObjectID();

			if (params.length > 0) {
				rpaction.put("baseobject", params[0]);
				rpaction.put("baseslot", params[1]);
				rpaction.put("baseitem", id);
			} else {
				rpaction.put("target", id);
			}

			client.send(rpaction);
		} else {
			super.onAction(client, action, params);
		}
	}

	@Override
	public int compare(Entity entity) {
		if (entity instanceof RPEntity) {
			return -1;
		} else if (entity instanceof Item) {
			return 0;
		}
		return 1;
	}
}
