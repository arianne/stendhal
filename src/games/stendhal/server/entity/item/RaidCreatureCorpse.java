package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;

/**
 * A faster rotting corpse for raid use
 */
public class RaidCreatureCorpse extends Corpse {
	// completely rot in 3 minutes
	private static final int DEGRADATION_STEP_TIMEOUT = 3 * 60 / 5;
	/**
	 * Create a corpse.
	 * 
	 * @param victim
	 *            The killed entity.
	 * @param killerName
	 *            The killer name.
	 */
	public RaidCreatureCorpse(final RPEntity victim, final String killerName) {
		super(victim, killerName);
	}
	
	@Override
	protected int getDegradationStepTimeout() {
		return DEGRADATION_STEP_TIMEOUT;
	}
}
