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
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * A portal which can be "used" by the player. Use a Door if you want some
 * sprites for it.
 */
public class Portal extends Entity {
	private boolean hidden = false;

	public Portal(RPObject object) throws AttributeNotFoundException {
		super(object);

		this.hidden = object.has("hidden");
	}

	@Override
	protected void loadSprite(RPObject object) {
		sprite = null;
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
		if (!hidden) {
			return ActionType.USE;
		}
		else return null;
	}

	@Override
	public void onAction(ActionType at, String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
		case USE:
			RPAction rpaction = new RPAction();
			rpaction.put("type", at.toString());
			int id = getID().getObjectID();
			rpaction.put("target", id);
			at.send(rpaction);
			break;

		default:
			super.onAction(at, params);
			break;
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

	@Override
	protected void buildOfferedActions(List<String> list) {
		// do not call super method because we do not want the
		// Look menu until some nice text are there to be looked
		// at.
		// super.buildOfferedActions(list);

		if (!hidden) {
			list.add(ActionType.USE.getRepresentation());
			if (StendhalClient.get().isAdmin()) {
				list.remove(ActionType.ADMIN_ALTER.getRepresentation());
			}
		}

	}
}
