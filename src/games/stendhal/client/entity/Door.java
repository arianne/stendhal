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
import games.stendhal.common.Direction;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class Door extends AnimatedEntity {

	private boolean open;

	private int orientation;


	public int getOrientation() {
		return orientation;
	}

	public boolean isOpen() {
		return open;
	}


	@Override
	protected void buildAnimations(final RPObject base) {
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

		int width;
		int height;
		if (direction.equals("n") || direction.equals("s")) {
			width = 3;
			height = 2;
		} else {
			width = 2;
			height = 3;
		}
		sprites.put("open", store.getAnimatedSprite("data/sprites/doors/" + clazz + "_" + direction + ".png", 0, 1,
		        width, height));
		sprites.put("close", store.getAnimatedSprite("data/sprites/doors/" + clazz + "_" + direction + ".png", 1, 1,
		        width, height));
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "close";
		return sprites.get("close")[0];
	}

	// When rpentity moves, it will be called with the data.
	@Override
	public void onMove(final int x, final int y, final Direction direction, final double speed) {
		if ((orientation == 1) || (orientation == 3)) {
			this.x = x - 1;
			this.y = y;
		} else {
			this.x = x;
			this.y = y - 1;
		}
	}

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("open")) {
			open = true;
			animation = "open";
		}
	}

	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
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
	public ActionType defaultAction() {
		if (open) {
			return ActionType.CLOSE;
		} else {
			return ActionType.OPEN;

		}
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
			case OPEN:
			case CLOSE:
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
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		if (open) {
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());

		}
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Door2DView(this);
	}
}
