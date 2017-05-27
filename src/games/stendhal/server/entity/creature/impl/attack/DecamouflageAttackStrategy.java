/**
 *
 */
package games.stendhal.server.entity.creature.impl.attack;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;


/**
 * The attacking creature loses its camouflage on attacking its target and becomes visible
 *
 * @author madmetzger
 */
class DecamouflageAttackStrategy implements AttackStrategy {

    private final AttackStrategy base;

    /**
     * Create a new DecamouflageAttackStrategy with a base strategy
     *
     * @param base
     */
    public DecamouflageAttackStrategy(AttackStrategy base) {
        this.base = base;
    }


    @Override
    public void getBetterAttackPosition(Creature creature) {
        this.base.getBetterAttackPosition(creature);
    }


    @Override
    public boolean hasValidTarget(Creature creature) {
        return this.base.hasValidTarget(creature);
    }

    @Override
    public void findNewTarget(Creature creature) {
        this.base.findNewTarget(creature);
    }

    @Override
    public boolean canAttackNow(Creature creature) {
        return this.base.canAttackNow(creature);
    }

    @Override
    public boolean canAttackNow(Creature attacker, RPEntity target) {
        return this.base.canAttackNow(attacker, target);
    }

    @Override
    public void attack(Creature creature) {
        creature.setVisibility(100);
        this.base.attack(creature);
    }

    @Override
    public int getRange() {
        return this.base.getRange();
    }

}
