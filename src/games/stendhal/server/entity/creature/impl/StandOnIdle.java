package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

public class StandOnIdle implements Idlebehaviour {
	public void perform(final Creature creature) {
		retreatUnderFire(creature);
	}
	
	/**
	 * Run away if under ranged fire, and unable to attack back.
	 * 
	 * @param creature The creature that should try to retreat.
	 * @return <code>true</code> if trying to escape, <code>false</code> if retreatin is not needed
	 */
	protected boolean retreatUnderFire(final Creature creature) {
		for (RPEntity attacker : creature.getAttackingRPEntities()) {
			if (attacker.canDoRangeAttack(creature)) {
				retreat(creature, attacker);

				return true;
			}
		}
		
		creature.setSpeed(0);
		
		return false;
	}
	
	/**
	 * Run away from an enemy.
	 * 
	 * @param creature The creature that tries to retreat.
	 * @param enemy The enemy to run away from.
	 */
	private void retreat(final Creature creature, final Entity enemy) {
		creature.clearPath();
		creature.faceToward(enemy);
		creature.setDirection(creature.getDirection().oppositeDirection());
		
		if (creature.getZone().collides(creature, creature.getX() + creature.getDirection().getdx(),
				creature.getY() + creature.getDirection().getdy(), true)) {
			// running against a wall; try turning
			if (Rand.rand(2) == 0) {
				creature.setDirection(creature.getDirection().nextDirection());
			} else {
				creature.setDirection(creature.getDirection().nextDirection().oppositeDirection());
			}
		}
		
		creature.setSpeed(creature.getBaseSpeed());
		creature.applyMovement();
	}
}
