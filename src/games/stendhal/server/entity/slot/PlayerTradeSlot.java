package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 * Slots of players which are use to offer items for trading.
 * 
 * @author hendrik
 */
public class PlayerTradeSlot extends PlayerSlot {

	/**
	 * Creates a new PlayerTradeSlot.
	 * 
	 * @param name name of slot
	 */
	public PlayerTradeSlot(final String name) {
		super(name);
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		return super.hasAsAncestor(entity);
	}

	@Override
	public boolean isTargetBoundCheckRequired() {
		return true;
	}

}
