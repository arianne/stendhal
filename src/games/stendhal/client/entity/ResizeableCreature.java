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

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/** A Creature which a server adjustable size */
public class ResizeableCreature extends Creature {
	private double width = 1.5;
	private double height = 1.0f;

	public double getHeight() {
		return height;
	}

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

		width = object.getDouble("width");
		height = object.getDouble("height");
	}


	//
	// RPObjectChangeListener
	//

	@Override
    public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		if (diff.has("width")) {
			width = diff.getDouble("width");
			changed();
		}

		if (diff.has("height")) {
			height = diff.getDouble("height");
			changed();
		}

		if (diff.has("metamorphosis")) {
			changed();
		}

		super.onChangedAdded(base, diff);
	}


	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		super.onChangedRemoved(base, diff);
		if (diff.has("metamorphosis")) {
			changed();
		}
	}


	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(width - 1) < .1) && (Math.abs(height - 2) < .1)) {
			return new Rectangle.Double(x, y + 1, 1, 1);
		}
		return new Rectangle.Double(x, y, width, height);
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
		return new ResizeableCreature2DView(this);
	}
}
