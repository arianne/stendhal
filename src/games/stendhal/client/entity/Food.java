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

public abstract class Food extends AnimatedEntity {
	private int amount;


	public Food() {
		animation = "0";
	}


	//
	// Food
	//

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("amount")) {
			int oldAmount = amount;
			animation = diff.get("amount");
			amount = diff.getInt("amount");

			// TODO this causes problems because of unidentified content refresh
			// events (e.g. synchronizing)
			if (amount > oldAmount) {
				playSound("fruit-regrow", 10, 25);
			}
		} else if (base.has("amount")) {
			animation = base.get("amount");
			amount = base.getInt("amount");
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}
}
