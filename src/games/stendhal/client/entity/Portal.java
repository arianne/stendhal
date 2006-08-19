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

public class Portal extends Entity {
	public Portal(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
	}

	protected void loadSprite(RPObject object) {
		sprite = null;
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public String defaultAction() {
		return "Use";
	}

	public String[] offeredActions() {
		String[] list = { "Use" };
		return list;
	}

	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Use")) {
			RPAction rpaction = new RPAction();
			rpaction.put("type", "use");
			int id = getID().getObjectID();
			rpaction.put("target", id);
			client.send(rpaction);
		}
	}

	public void draw(GameScreen screen) {
	}

	public int compare(Entity entity) {
		return -1;
	}
}
