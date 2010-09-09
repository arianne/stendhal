package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

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

	@Override
	protected int add(RPObject object, boolean assignId) {
		((Player) getOwner()).unlockTradeItemOffer();
		return super.add(object, assignId);
	}

	@Override
	public RPObject remove(ID id) {
		((Player) getOwner()).unlockTradeItemOffer();
		return super.remove(id);
	}

	// TODO: check modifications to stackable items
}
