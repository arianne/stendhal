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
		TradeState tradeState = ((Player) getOwner()).getTradeState();
		if (tradeState == TradeState.NO_ACTIVE_TRADE || tradeState == TradeState.OFFERING_TRADE) {
			setErrorMessage("You are not trading with someone at the moment.");
			return false;
		}
		if (tradeState != TradeState.MAKING_OFFERS) {
			setErrorMessage("Your trade offer was been locked. You can cancel the trade if you want to modify it.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isTargetBoundCheckRequired() {
		return true;
	}
}