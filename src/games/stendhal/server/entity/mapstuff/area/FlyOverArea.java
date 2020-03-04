/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * An entity that acts as an obstacle for "walking" entities.
 *
 * FIXME: Players should not be able to set items in this area
 *        as is done with WalkBlocker.
 */
public class FlyOverArea extends WalkBlocker {

	public static void generateRPClass() {
		final RPClass flyover = new RPClass("flyover");
		flyover.isA("entity");
		flyover.addAttribute("walk_blocker", Type.FLAG, Definition.VOLATILE);
	}

	/**
	 * Create an area that can be "flown" over.
	 */
	public FlyOverArea() {
		super();

		setRPClass("flyover");
		put("type", "flyover");
		put("walk_blocker", "");

		setResistance(0);
		setVisibility(0);
	}

	@Override
	public boolean isObstacle(final Entity entity) {
		if (entity.has("flying")) {
			return false;
		}

		return true;
	}
}
