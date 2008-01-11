package games.stendhal.server.entity.creature;

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
	public RaidCreature(Creature copy) {
		super(copy);
	}

	@Override
	public Creature getInstance() {
		return new RaidCreature(this);
	}
}
