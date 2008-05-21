package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.creature.Creature;
import marauroa.common.Log4J;

class Patroller implements Idlebehaviour {
	/** the logger instance. */
	private static final marauroa.common.Logger logger = Log4J.getLogger(Patroller.class);

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	


	public void startIdleness(Creature creature) {
		minX = creature.getX() - 3;
		maxX = creature.getX() + 3;
		minY = creature.getY() - 3;
		maxY = creature.getY() + 3;
	}

	public void perform(Creature creature) {
		if (creature.hasPath()) {
			creature.followPath();
		} else {
			Direction d;
			if (Rand.rand(3) == 1) {
				d = Direction.rand();
			} else {
				d = creature.getDirection();
			}
			
			/*
			 * We want to avoid an endless loop.
			 */
			int i=0;
			while (i<4 && weWouldLeaveArea(creature, d)) {
				d = d.nextDirection();
				i++;
			}
			
			creature.setDirection(d);
			creature.setSpeed(creature.getBaseSpeed());
            creature.applyMovement();
		}
		creature.applyMovement();
	}

	private boolean weWouldLeaveArea(Creature creature, Direction d) {
		return (creature.getY() + d.getdy() <= minY)
				|| (creature.getY() + d.getdy() >= maxY)
				|| (creature.getX() + d.getdx() <= minX)
				|| (creature.getX() + d.getdx() >= maxX);
	}

}
