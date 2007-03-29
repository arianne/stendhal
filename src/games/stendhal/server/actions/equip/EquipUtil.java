package games.stendhal.server.actions.equip;

import java.util.List;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Useful method to deal with equipable items
 */
public class EquipUtil {
	private static Logger logger = Logger.getLogger(EquipUtil.class);

	/**
	 * Gets the object for the given id. Returns null when the item is not
	 * available. Failure is written to the logger.
	 * 
	 * @param player
	 *            the player
	 * @param objectId
	 *            the objects id
	 * @return the object with the given id or null if the object is not
	 *         available.
	 */
	static Entity getEntityFromId(Player player, int objectId) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
		RPObject.ID id = new RPObject.ID(objectId, zone.getID());

		if (!zone.has(id)) {
			logger.debug("Rejected because zone doesn't have object " + objectId);
			return null;
		}

		return (Entity) zone.get(id);
	}


	/**
	 * Checks if the object is of one of the given class or one of its children.
	 * 
	 * @param validClasses
	 *            list of valid class-objects
	 * @param object
	 *            the object to check
	 * @return true when the class is in the list, else false
	 */
	static boolean isCorrectClass(List<Class> validClasses, RPObject object) {
		for (Class clazz : validClasses) {
			if (clazz.isInstance(object)) {
				return true;
			}
		}
		logger.debug("object " + object.getID() + " is not of the correct class. it is " + object.getClass().getName());
		return false;
	}
}
