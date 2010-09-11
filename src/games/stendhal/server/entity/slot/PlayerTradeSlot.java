package games.stendhal.server.entity.slot;

import games.stendhal.common.TradeState;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

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
	    if (((Player) getOwner()).getTradeState() != TradeState.MAKING_OFFERS) {
	        return false;
	    }
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isTargetBoundCheckRequired() {
		return true;
	}
}