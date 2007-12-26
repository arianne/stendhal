package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.chest.PersonalChest;

import org.apache.log4j.Logger;

/**
 * a slot of a personal chest
 *
 * @author hendrik
 */
public class PersonalChestSlot extends LootableSlot {
	private static Logger logger = Logger.getLogger(PersonalChestSlot.class);
	private PersonalChest chest;

	public PersonalChestSlot(PersonalChest owner) {
	    super(owner);
	    this.chest = owner;
    }

	@Override
    public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
	    if (chest.getAttending() != entity) {
	    	logger.error(entity + " tried to take stuff out of the bank chest " + chest + " which is currently attenting " + chest.getAttending());
	    	return false;
	    }
	    return super.isReachableForTakingThingsOutOfBy(entity);
    }

	
}
