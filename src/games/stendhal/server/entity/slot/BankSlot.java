package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 * a bank slot
 *
 * @author hendrik
 */
public class BankSlot extends PlayerSlot {

	/**
	 * creates a new keyed slot
	 *
	 * @param bank Bank
	 */
	public BankSlot(Banks bank) {
		super(bank.getSlotName());
	}

	@Override
	public boolean isReachableBy(Entity entity) {
		// TODO: Check if we are next to a chest which acts as an interface
		//       to this bank slot
		boolean found = false;
		if (!found) {
			return false;
		}
		return super.isReachableBy(entity);
	}

	
}
