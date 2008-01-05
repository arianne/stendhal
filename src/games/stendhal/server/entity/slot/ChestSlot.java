package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.chest.Chest;

import org.apache.log4j.Logger;

/**
 * a slot of a chest which is only accessable if the chest is open
 *
 * @author hendrik
 */
public class ChestSlot extends LootableSlot {
	private static Logger logger = Logger.getLogger(ChestSlot.class);
	private Chest chest;

	public ChestSlot(Chest owner) {
	    super(owner);
	    this.chest = owner;
    }

	@Override
    public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		if (!chest.isOpen()) {
	    	logger.error(entity + " tried to take stuff out of the closed chest " + chest);
	    	return false;
	    }
	    return super.isReachableForTakingThingsOutOfBy(entity);
    }
}