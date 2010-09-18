package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.chest.Chest;

import org.apache.log4j.Logger;

/**
 * A slot of a chest which is only accessible, if the chest is open.
 * 
 * @author hendrik
 */
public class ChestSlot extends LootableSlot {
	private static Logger logger = Logger.getLogger(ChestSlot.class);
	private final Chest chest;

	/**
	 * Creates a ChestSlot
	 * 
	 * @param owner
	 *            Chest owning this slot
	 */
	public ChestSlot(final Chest owner) {
		super(owner);
		this.chest = owner;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!chest.isOpen()) {
			setErrorMessage("This " + ((Entity)getOwner()).getDescriptionName(true) + " is not open.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}
}
