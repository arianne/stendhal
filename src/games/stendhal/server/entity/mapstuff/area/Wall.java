/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
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
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * An entity that just acts as a visible obstacle like a wall.
 */
public class Wall extends AreaEntity {

	/**
	 * Create a wall.
	 *
	 * @param width width
	 * @param height height
	 */
	public Wall(int width, int height) {
		super(width, height);

		setRPClass("wall");
		put("type", "wall");
		setResistance(100);
	}


	public static void generateRPClass() {
		final RPClass clazz = new RPClass("wall");
		clazz.isA("area");
		clazz.addAttribute("class", Type.STRING);
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
