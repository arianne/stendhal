/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import games.stendhal.common.Direction;
import games.stendhal.server.entity.creature.Creature;

class Patroller extends StandOnIdle {
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	/**
	 * Recalculate limits of the patrolling area.
	 *
	 * @param creature
	 */
	private void initArea(final Creature creature) {
		minX = creature.getX() - 3;
		maxX = creature.getX() + 2 + (int) (creature.getWidth());
		minY = creature.getY() - 3;
		maxY = creature.getY() + 2 + (int) (creature.getHeight());
	}

	@Override
	public void perform(final Creature creature) {
		if (!creature.getZone().getPlayerAndFriends().isEmpty()) {
			if (creature.hasPath()) {
				creature.followPath();
			} else if (!retreatUnderFire(creature)) {
				/*
				 * Move before turning, so that the turning looks smooth to the
				 * client.
				 */
				if (creature.getDirection() != Direction.STOP) {
					creature.setSpeed(creature.getBaseSpeed());
				}
				creature.applyMovement();

				/*
				 * Check if the creature is outside the patrolling area (ie.
				 * followed or escaped a player).
				 */
				if (weWouldLeaveArea(creature, Direction.STOP)) {
					initArea(creature);
				}
				Direction currentDir = creature.getDirection();
				if ((currentDir == Direction.STOP)
						|| weWouldLeaveArea(creature, creature.getDirection())
						|| creature.getZone().collides(creature, creature.getX() + currentDir.getdx(),
								creature.getY() + currentDir.getdy())) {
					for (int i = 0; i < 4; i++) {
						currentDir = currentDir.nextDirection();

						if (!weWouldLeaveArea(creature, currentDir)
								&& !creature.getZone().collides(creature, creature.getX() + currentDir.getdx(),
										creature.getY() + currentDir.getdy())) {
							creature.setDirection(currentDir);
							continue;
						}
					}
				}
				// already moved.
				return;
			}
			creature.applyMovement();
		}
	}

	/**
	 * Check if next step would move the creature outside the patrolling area.
	 *
	 * @param creature
	 * @param d
	 * @return <code>true</code> if the creature would leave the area,
	 * 	<code>false</code> otherwise
	 */
	private boolean weWouldLeaveArea(final Creature creature, final Direction d) {
		return (creature.getY() + d.getdy() < minY)
				|| (creature.getY() + d.getdy() > maxY)
				|| (creature.getX() + d.getdx() < minX)
				|| (creature.getX() + d.getdx() > maxX);
	}
}
