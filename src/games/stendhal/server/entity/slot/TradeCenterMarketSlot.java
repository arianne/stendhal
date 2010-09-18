package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * the slot of the trade center in which all the offered items are stored
 * 
 * @author hendrik
 */
public class TradeCenterMarketSlot extends EntitySlot {

	/**
	 * Creates a new TradeCenterMarketSlot.
	 * 
	 */
	public TradeCenterMarketSlot() {
		super();
	}

	/**
	 * Creates a new TradeCenterMarketSlot.
	 * 
	 * @param name
	 *            name of slot
	 */
	public TradeCenterMarketSlot(final String name) {
		super(name);
	}

	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!(entity instanceof SpeakerNPC)) {
			setErrorMessage("Only the trade manager may access this " + getName());
			return false;
		}
		return true;
	}

	public boolean isReachableForThrowingThingsIntoBy(final Entity entity) {
		return isReachableForTakingThingsOutOfBy(entity);
	}

	public boolean isItemSlot() {
		return true;
	}

	public boolean isTargetBoundCheckRequired() {
		return true;
	}


	/**
	 * gets the type of the slot ("slot", "ground", "market")
	 *
	 * @return slot type
	 */
	public String getSlotType() {
		return "marget";
	}
}
