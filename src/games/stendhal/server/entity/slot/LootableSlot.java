package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 * A lootable slot of some creature.
 * 
 * @author hendrik
 */
public class LootableSlot extends EntitySlot {
	private final Entity owner;

	/**
	 * creates a new lootable slot.
	 * 
	 * @param owner
	 *            owner of this Slot
	 */
	public LootableSlot(final Entity owner) {
		super("content");
		this.owner = owner;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		return entity.nextTo(owner);
	}

}
