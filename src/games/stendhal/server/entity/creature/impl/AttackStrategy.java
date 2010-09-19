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
package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public interface AttackStrategy {

	boolean hasValidTarget(Creature creature);

	void findNewTarget(Creature creature);

	void getBetterAttackPosition(Creature creature);

	boolean canAttackNow(Creature creature);

	void attack(Creature creature);



}
