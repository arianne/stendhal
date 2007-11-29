package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 * a lootable slot of some creature
 *
 * @author hendrik
 */
public class LootableSlot extends EntitySlot {
	private Entity owner;

	/**
	 * creates a new lootable slot
	 *
	 * @param owner owner of this Slot
	 */
	public LootableSlot(Entity owner) {
		super("content");
		this.owner = owner;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		return entity.nextTo(owner);
	}

}
