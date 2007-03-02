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
import games.stendhal.common.Direction;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Door extends AnimatedEntity {
	private boolean open;

	private int orientation;


	public Door( RPObject base)
			throws AttributeNotFoundException {
		super( base);

	}

	@Override
	protected void buildAnimations(RPObject base) {
		SpriteStore store = SpriteStore.get();

		String clazz = base.get("class");
		String direction = null;

		orientation = base.getInt("dir");
		switch (orientation) {
		case 4:
			direction = "w";
			break;
		case 2:
			direction = "e";
			break;
		case 1:
			direction = "n";
			break;
		case 3:
			direction = "s";
			break;
		}

		sprites.put("open", store.getAnimatedSprite("data/sprites/doors/"
				+ clazz + "_" + direction + ".png", 0, 1, 3, 2));
		sprites.put("close", store.getAnimatedSprite("data/sprites/doors/"
				+ clazz + "_" + direction + ".png", 1, 1, 3, 2));
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "close";
		return sprites.get("close")[0];
	}

	// When rpentity moves, it will be called with the data.
	@Override
	public void onMove(int x, int y, Direction direction, double speed) {
		if ((orientation == 1) || (orientation == 3)) {
			this.x = x - 1;
			this.y = y;
		} else {
			this.x = x;
			this.y = y - 1;
		}
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("open")) {
			open = true;
			animation = "open";
		}
	}

	@Override
	public void onChangedRemoved(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedRemoved(base, diff);

		if (diff.has("open")) {
			open = false;
			animation = "close";
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
	public String defaultAction() {
		return "Open";
	}


	protected void buildOfferedActions(List list) {
		list.add("Look");
		list.add("Open");

		if(open) {
			list.add("Close");
		}
	}


	@Override
	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Open") || action.equals("Close")) {
			
			RPAction rpaction = new RPAction();
			rpaction.put("type", "use");
			int id = getID().getObjectID();
			rpaction.put("target", id);
			client.send(rpaction);
		} else {
			super.onAction(client, action, params);
		}
	}

	@Override
	public int getZIndex() {
		return 5000;
	}
}
