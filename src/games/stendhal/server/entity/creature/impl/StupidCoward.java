package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

/**
 * 
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
