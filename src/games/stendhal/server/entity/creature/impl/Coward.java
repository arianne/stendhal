package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public class Coward extends HandToHand {
	@Override
	public void getBetterAttackPosition(Creature creature) {
		super.getBetterAttackPosition(creature);
		if (creature.isAttacked()){
			 creature.faceToward(creature.getAttackSources().get(0));
			 creature.setDirection(creature.getDirection().oppositeDirection());
			 creature.setSpeed(creature.getBaseSpeed());
		}
	}

	
}
