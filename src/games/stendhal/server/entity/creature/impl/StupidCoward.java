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

	@Override
	public void getBetterAttackPosition(final Creature creature) {

		if (creature.isAttacked()) {
			creature.clearPath();
			creature.faceToward(creature.getAttackSources().get(0));
			creature.setDirection(creature.getDirection().oppositeDirection());
			creature.setSpeed(creature.getBaseSpeed());
		} else {
			super.getBetterAttackPosition(creature);
		}
	}

}
