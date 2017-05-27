/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.events;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * Event for requesting change in the map area the client displays. Normally
 * the view is centered on the player but this event tells the client to use
 * a different center point.
 */
public class ViewChangeEvent extends RPEvent {
	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.VIEW_CHANGE);
		rpclass.addAttribute("x", Type.INT);
		rpclass.addAttribute("y", Type.INT);
	}

	/**
	 * Create a new view change event.
	 *
	 * @param x x coordinate of the requested view center
	 * @param y y coordinate of the requested view center
	 */
	public ViewChangeEvent(int x, int y) {
		super(Events.VIEW_CHANGE);
		put("x", x);
		put("y", y);
	}
}
