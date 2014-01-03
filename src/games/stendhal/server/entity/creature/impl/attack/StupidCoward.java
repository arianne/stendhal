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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

/**
 *  Implements the ai-profile stupid coward.
 *  <p>
 * &lt;ai&gt;
 * <p>
 * &lt;profile name="stupid coward"/&gt;
 * <p>
 * &lt;/ai&gt;
 */
public class StupidCoward extends HandToHand {

	private static final int SCARED_OF_LEVEL = 7;

	@Override
	public void getBetterAttackPosition(final Creature creature) {

		if (isAttackedByStrongEnemy(creature)) {
			creature.clearPath();
			creature.faceToward(creature.getAttackSources().get(0));
			creature.setDirection(creature.getDirection().oppositeDirection());
			creature.setSpeed(creature.getBaseSpeed());
		} else {
			super.getBetterAttackPosition(creature);
		}
	}

	/**
	 * checks whether the specified entity is attacked by a strong enemy
	 *
	 * @param creature creature to check
	 * @return true, if a strong enemy is attacking; false otherwise
	 */
	private boolean isAttackedByStrongEnemy(final Creature creature) {
		for (Entity entity : creature.getAttackSources()) {
			if (! (entity instanceof RPEntity)) {
				continue;
			}

			if (((RPEntity) entity).getLevel() >= SCARED_OF_LEVEL) {
				return true;
			}
		}
		return false;
	}
}
