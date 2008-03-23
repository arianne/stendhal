package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.creature.Creature;

class Patroller implements Idlebehaviour {

//	TODO: make the creatures stay in a certain regionprivate final static Node[] nodes = new Node[]{ new Node(0, 0), new Node(-6, 0), new Node(-6, 6), new Node(0, 6) };

	public void perform(Creature creature) {
		if (creature.hasPath()) {
			creature.followPath();
		} else {
			
			
			creature.setDirection(Direction.rand());
			creature.setSpeed(creature.getBaseSpeed());
			
		}
		creature.applyMovement();
	}

}
