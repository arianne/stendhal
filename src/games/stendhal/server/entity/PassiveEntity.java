/* $Id$ */
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
package games.stendhal.server.entity;

import marauroa.common.game.RPObject;

/**
 * An entity that doesn't move on it's own, but can be moved.
 */
public abstract class PassiveEntity extends Entity {
	/**
	 * Create a passive entity.
	 */
	public PassiveEntity() {
		setResistance(0);
	}

	/**
	 * Create a passive entity.
	 *
	 * @param object
	 *            The template object.
	 */
	public PassiveEntity(final RPObject object) {
		super(object);

		setResistance(0);
	}
}
