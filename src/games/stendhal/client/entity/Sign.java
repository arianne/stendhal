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

import games.stendhal.client.GameObjects;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalUI;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class Sign extends Entity {

	private String text;

	// Give Signs same color on Screen and Log window. intensifly@gmx.com
	private static final Color signColor = new Color(0x006400); // dark green

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("text")) {
			text = diff.get("text");
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}


	@Override
	public ActionType defaultAction() {
		return ActionType.READ;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		// we don't want "Look", we use "Read" instead.
		// super.buildOfferedActions(list);
		list.add(ActionType.READ.getRepresentation());
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// =handleAction(action);
		switch (at) {
			case READ:
				GameObjects.getInstance().addText(
					this, text, signColor, false);

				if (text.contains("\n")) {
					// The sign's text has multiple lines. Add a linebreak after
					// "you read" so that it is easier readable.
					StendhalUI.get().addEventLine("You read:\n\"" + text + "\"", signColor);
				} else {
					StendhalUI.get().addEventLine("You read: \"" + text + "\"", signColor);
				}
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
		return new Sign2DView(this);
	}
}
