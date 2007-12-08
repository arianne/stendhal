package games.stendhal.server.util;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.RPObject;

/**
 * Utilities to handle entities in the server
 * @author Martin Fuchs
 */
public class EntityHelper
{
	/**
	 * Translate the "target" parameter of actions like "look" into an entity reference.
	 * Numeric parameters are treated as object IDs, alphanumeric names are searched in
	 * the list of players and NPCs.
	 * @param entity name
	 * @param zone to search for objects
	 * @return
	 */
	public static Entity entityFromTargetName(String name, StendhalRPZone zone)
    {
		Entity entity = null;

	    if (name!=null && name.length()>0 && Character.isDigit(name.charAt(0))) {
	    	int objectId = Integer.parseInt(name);

	    	RPObject.ID targetid = new RPObject.ID(objectId, zone.getID());

	    	if (zone.has(targetid)) {
	    		RPObject object = zone.get(targetid);

	    		if (object instanceof Entity) {
	    			entity = (Entity) object;
	    		}
	    	}
	    }

	    if (entity == null) {
	    	entity = StendhalRPRuleProcessor.get().getPlayer(name);
	    }

	    if (entity == null) {
	    	entity = NPCList.get().get(name);
	    }

	    if (entity!=null && entity.getZone()==zone)
	    	return entity;
	    else
	    	return null;
    }
}
