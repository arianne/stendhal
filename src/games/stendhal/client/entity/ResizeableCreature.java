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

import marauroa.common.game.RPObject;

/** A Creature which a server adjustable size */
public class ResizeableCreature extends Creature {
	/**
	 * The entity width.
	 */
	private double width;

	/**
	 * The entity height.
	 */
	private double height;


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
		return new ResizeableCreature2DView(this);
	}


	/**
	 * Get the area the entity occupies.
	 *
	 * @return	A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(width - 1) < .1) && (Math.abs(height - 2) < .1)) {
			return new Rectangle.Double(getX(), getY() + 1, 1, 1);
		}

		return super.getArea();
	}

	/**
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	@Override
	public double getHeight() {
		return height;
	}

	/**
	 * Get the entity width.
	 *
	 * @return	The width.
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

		if(object.has("height")) {
			height = object.getDouble("height");
		} else {
			height = 1.0;
		}

		if(object.has("width")) {
			width = object.getDouble("width");
		} else {
			width = 1.5;
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

		if (changes.has("width")) {
			width = changes.getDouble("width");
			changed();
		}

		if (changes.has("height")) {
			height = changes.getDouble("height");
			changed();
		}
	}
}
