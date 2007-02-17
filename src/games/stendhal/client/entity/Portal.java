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

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * A portal which can be "used" by the player. Use a Door
 * if you want some sprites for it.
 */
public class Portal extends Entity {
	private boolean hidden = false;

	public Portal( RPObject object)
			throws AttributeNotFoundException {
		super( object);

		this.hidden = object.has("hidden");
	}

	@Override
	protected void loadSprite(RPObject object) {
		sprite = null;
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public String defaultAction() {
		return "Use";
	}

	@Override
	public String[] offeredActions() {
		List<String> list = new ArrayList<String>();
		if (!hidden) {
			list.add("Use");
			if (client.isAdmin()) {
				list.add("(*)Inspect");
				list.add("(*)Destroy");
			}
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Use")) {
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
	public void draw(GameScreen screen) {
		// portals are invisible; use a Door to get a changing sprite
	}

	@Override
	public int getZIndex() {
		return 5000;
	}
}
