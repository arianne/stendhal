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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class GrainField extends AnimatedStateEntity {
	private int width;

	private int height;


	public GrainField()  {
		this(1, 2);
	}


	public GrainField(int width, int height)  {
		this.width = width;
		this.height = height;
		animation = "0";
	}

	public double getHeight() {
		return height;
	}


	public double getWidth() {
		return width;
	}


	public void init(final RPObject object) {
		super.init(object);

		// default values are for compatibility to server <= 0.56

		if (object.has("width")) {
			width = object.getInt("width");
		}
		if (object.has("height")) {
			height = object.getInt("height");
		}
	}


	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("ripeness")) {
			animation = diff.get("ripeness");
			changed();
		} else if (base.has("ripeness")) {
			animation = base.get("ripeness");
		}
	}

	//

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + height - 1, width, 1);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.HARVEST;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		list.add(ActionType.HARVEST.getRepresentation());
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at=handleAction(action);
		switch (at) {
			case HARVEST:
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


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new GrainField2DView(this);
	}
}
