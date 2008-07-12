package games.stendhal.server.entity.creature.impl;

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
