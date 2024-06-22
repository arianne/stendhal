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
package games.stendhal.server.entity.creature.impl.idle;

import games.stendhal.server.entity.creature.Creature;


public interface IdleBehaviour {

	void perform(Creature creature);

	/**
	 * Can be called to execute tile collision behavior.
	 *
	 * @param creature
	 *   Entity being acted on.
	 * @param nx
	 *   Horizontal position where entity collides.
	 * @param ny
	 *   Vertical position where entity collides.
	 * @return
	 *   {@code true} if collision handling should not propagate.
	 */
	boolean handleSimpleCollision(Creature creature, int nx, int ny);

	/**
	 * Can be called to execute entity collision behavior.
	 *
	 * @param creature
	 *   Entity being acted on.
	 * @return
	 *   {@code true} if collision handling should not propagate.
	 */
	boolean handleObjectCollision(Creature creature);
}
