package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.RaidCreatureCorpse;

/**
 * A Raid creature is a creature that doesn't make players killed by it to lose
 * any XP, ATK or DEF.
 * 
 * @author miguel
 * 
 */
public class RaidCreature extends Creature {
	/**
	 * RaidCreature.
	 * 
	 * @param copy
	 *            creature to wrap
	 */
	public RaidCreature(final Creature copy) {
		super(copy);
	}

	@Override
	public Creature getNewInstance() {
		return new RaidCreature(this);
	}
	
	@Override
	protected Corpse makeCorpse(String killer) {
		// use a faster rotting corpse as raids get quite messy
		return new RaidCreatureCorpse(this, killer);
	}
}
