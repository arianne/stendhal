package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.creature.Creature;

class Patroller implements Idlebehaviour {
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
			if (Rand.throwCoin() == 1) {
				 d = Direction.rand();
			} else {
				d = creature.getDirection();
			}
			while (weWouldLeaveArea(creature, d)) {
				d = d.nextDirection();
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
