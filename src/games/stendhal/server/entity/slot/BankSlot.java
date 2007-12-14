package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

import java.util.List;

/**
 * a bank slot
 * 
 * @author hendrik
 */
public class BankSlot extends PlayerSlot {
	private Banks bank;

	/**
	 * creates a new keyed slot
	 * 
	 * @param bank
	 *            Bank
	 */
	public BankSlot(Banks bank) {
		super(bank.getSlotName());
		this.bank = bank;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		// Check if we are next to a chest which acts as an interface
		// to this bank slot
		List<Entity> accessors = BankAccessorManager.get().get(bank);
		boolean found = false;
		for (Entity accessor : accessors) {
			if (entity.nextTo(accessor)) {
				found = true;
				break;
			}
		}

		if (!found) {
			// sorry, we are not near a personal chest
			return false;
		}

		// now check that it is the slot of the right player
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

}
