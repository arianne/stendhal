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

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * A grain field entity.
 */
public class GrainField extends Entity {
	/**
	 * The entity width.
	 */
	private int width;

	/**
	 * The entity height.
	 */
	private int height;

	/**
	 * Current ripeness.
	 */
	private String ripeness;

	/**
	 * The maximum ripeness.
	 */
	protected int	maxRipeness;


	/**
	 * Create a grain field.
	 */
	public GrainField()  {
		this(1, 2);
	}


	/**
	 * Create a grain field.
	 *
	 * @param	width		The entity width.
	 * @param	height		The entity height.
	 */
	public GrainField(int width, int height)  {
		this.width = width;
		this.height = height;
	}


	//
	// GrainField
	//

	/**
	 * Get the maximum ripeness.
	 *
	 * @return	The maximum ripeness.
	 */
	public int getMaximumRipeness() {
		return maxRipeness;
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	@Override
	protected Entity2DView createView() {
		return new GrainField2DView(this);
	}


	/**
	 * Get the area the entity occupies.
	 *
	 * @return	A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(getX(), getY() + getHeight() - 1, getWidth(), 1);
	}


	/**
	 * Get the entity height.
	 *
	 * @return	The entity height.
	 */
	@Override
	public double getHeight() {
		return height;
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	public String getState() {
		return ripeness;
	}


	/**
	 * Get the entity width.
	 *
	 * @return	The entity width.
	 */
	@Override
	public double getWidth() {
		return width;
	}


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		// default values are for compatibility to server <= 0.56

		if (object.has("width")) {
			width = object.getInt("width");
		}

		if (object.has("height")) {
			height = object.getInt("height");
		}

		if (object.has("ripeness")) {
			ripeness = object.get("ripeness");
		} else {
			ripeness = "0";
		}

		// default values are for compatibility to server <= 0.56
		if (object.has("max_ripeness")) {
			maxRipeness = object.getInt("max_ripeness");
		} else {
			maxRipeness = 5;
		}
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (changes.has("ripeness")) {
			ripeness = changes.get("ripeness");
			changed();
		}

		if (object.has("max_ripeness")) {
			maxRipeness = object.getInt("max_ripeness");
			changed();
		}
	}

	//
	//

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
}
