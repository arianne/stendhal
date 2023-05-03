/***************************************************************************
 *                    Copyright © 2007-2023 - Stendhal                     *
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
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * An entity that just acts as an obstacle. This is a temporary workaround to
 * allow items to be placed, but not players/entities, until multi-level
 * collisions can be added.
 */
public class WalkBlocker extends AreaEntity {
	/**
	 * Create a walk blocker.
	 */
	public WalkBlocker() {
		super(1, 1);

		setRPClass("walkblocker");
		put("type", "walkblocker");
		// Count as collision for the client and pathfinder
		setResistance(100);
	}


	public static void generateRPClass() {
		final RPClass blocker = new RPClass("walkblocker");
		blocker.isA("area");
		blocker.addAttribute("class", Type.STRING);
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if the other entity is an RPEntity, otherwise
	 *         the default.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		if (entity instanceof RPEntity) {
			return true;
		}

		return super.isObstacle(entity);
	}
}
