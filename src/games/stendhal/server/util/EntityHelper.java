package games.stendhal.server.util;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.RPObject;

/**
 * Utilities to handle entities in the server
 * 
 * @author Martin Fuchs
 */
public class EntityHelper {
	/**
	 * Translate the "target" parameter of actions like "look" into an entity
	 * reference. Numeric parameters are treated as object IDs, alphanumeric
	 * names are searched in the list of players and NPCs.
	 * 
	 * @param target
	 *            representation of the target
	 * @param zone
	 *            to search for objects
	 * @return
	 */
	public static Entity entityFromTargetName(String target, StendhalRPZone zone) {
		Entity entity = null;

		if (target != null && target.length() > 1 && target.charAt(0) == '#'
				&& Character.isDigit(target.charAt(1))) {
			int objectId = Integer.parseInt(target.substring(1));

			RPObject.ID targetid = new RPObject.ID(objectId, zone.getID());

			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);

				if (object instanceof Entity) {
					entity = (Entity) object;
				}
			}
		}

		if (entity == null) {
			entity = StendhalRPRuleProcessor.get().getPlayer(target);
		}

		if (entity == null) {
			entity = NPCList.get().get(target);
		}

		if (entity != null && entity.getZone() == zone) {
			return entity;
		} else {
			return null;
		}
	}
}
