/**
 *
 */
package games.stendhal.server.entity.creature.impl.idle;

import games.stendhal.server.entity.creature.Creature;

/**
 * An idle behaviour, where the idling creature stays invisible
 *
 * @author madmetzger
 */
class CamouflagedIdleBehaviour implements IdleBehaviour {

    private final IdleBehaviour base = new Patroller();

    @Override
    public void perform(Creature creature) {
        creature.setVisibility(50);
        this.base.perform(creature);
    }

}
