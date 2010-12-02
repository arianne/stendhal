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
package games.stendhal.server.entity.creature.impl.attack;

import games.stendhal.server.entity.creature.Creature;

public class Coward extends HandToHand {
	@Override
	public void getBetterAttackPosition(final Creature creature) {

		if (creature.isAttacked()) {
			creature.clearPath();
			creature.faceToward(creature.getAttackSources().get(0));
			creature.setDirection(creature.getDirection().oppositeDirection());
			if (creature.getZone().collides(creature, creature.getX() + creature.getDirection().getdx(),
					creature.getY() + creature.getDirection().getdy(), true)) {
				creature.setDirection(creature.getDirection().nextDirection());
			}
			creature.setSpeed(creature.getBaseSpeed());

		} else {
			super.getBetterAttackPosition(creature);
		}
	}

}
