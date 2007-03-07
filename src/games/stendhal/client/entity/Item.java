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

import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class Item extends PassiveEntity {

	public Item(RPObject object) throws AttributeNotFoundException {
		super(object);
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
	public ActionType defaultAction() {
		return ActionType.USE;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		list.add("Use");
		super.buildOfferedActions(list);
	}

	@Override
	public void onAction(ActionType at, String... params) {
		// ActionType at =handleAction(action);

		switch (at) {
		case USE:
			RPAction rpaction = new RPAction();
			rpaction.put("type", at.toString());
			int id = getID().getObjectID();

			if (params.length > 0) {
				rpaction.put("baseobject", params[0]);
				rpaction.put("baseslot", params[1]);
				rpaction.put("baseitem", id);
			} else {
				rpaction.put("target", id);
			}

			at.send(rpaction);
			break;

		default:
			super.onAction(at, params);
			break;
		}

	}

	@Override
	public int getZIndex() {
		return 7000;
	}
}
