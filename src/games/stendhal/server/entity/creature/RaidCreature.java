package games.stendhal.server.entity.creature;



public class RaidCreature extends Creature {
	/**
	 * RaidCreature
	 *
	 * @param copy creature to wrap
	 */
	public RaidCreature(Creature copy) {
		super(copy);
	}
	
	@Override
	public Creature getInstance() {
		return new RaidCreature(this);
	}
}
