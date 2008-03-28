package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public class Coward extends HandToHand {
	@Override
	public void getBetterAttackPosition(Creature creature) {
		creature.faceToward(creature.getAttackTarget());
		creature.setDirection(creature.getDirection().oppositeDirection());
		creature.setSpeed(creature.getBaseSpeed());
	}

	
}
