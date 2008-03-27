package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
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
			Direction d = Direction.rand();
			// If we're going to leave the patrol region,
			// Use a different direction.
			while (((d == Direction.UP) && (creature.getY() <= minY))
					|| ((d == Direction.DOWN) && (creature.getY() >= maxY))
					|| ((d == Direction.LEFT) && (creature.getX() <= minX))
					|| ((d == Direction.RIGHT) && (creature.getX() >= maxX))) {
				d = d.nextDirection();
			}
			
			creature.setDirection(d);
			creature.setSpeed(creature.getBaseSpeed());
                        creature.applyMovement();
		}
		creature.applyMovement();
	}

}
