package games.stendhal.server.util;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Utilities to handle entities in the server
 * 
 * @author Martin Fuchs
 */
public class EntityHelper {
	private static final String ATTR_BASESLOT = "baseslot";
	private static final String ATTR_BASEOBJECT = "baseobject";
	private static final String ATTR_BASEITEM = "baseitem";
	
	/**
	 * Translate the "target" parameter of actions like "look" into an entity
	 * reference. Numeric parameters are treated as object IDs, alphanumeric
	 * names are searched in the list of players and NPCs.
	 * 
	 * @param target
	 *            representation of the target
	 * @param player
	 *            to constraint for current zone and screen area
	 * @return the entity associated either with name or id or
	 *         <code> null </code> if none was found.
	 */
	public static Entity entityFromTargetName(String target, Entity player) {
		StendhalRPZone zone = player.getZone();
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
			// check distance: 640x480 client screen size for 32x32 pixel tiles
			// -> makes 20x15 tiles screen size
			if (Math.abs(entity.getX() - player.getX()) <= 20 &&
				Math.abs(entity.getY() - player.getY()) <= 15) {
				return entity;
			}
		}

		return null;
	}

	public static Entity entityFromSlot(Player player, RPAction action) {
		// entity in a slot?
		if (!action.has(ATTR_BASEITEM) 
				|| !action.has(ATTR_BASEOBJECT)
				|| !action.has(ATTR_BASESLOT)) {
			return null;
		}

		StendhalRPZone zone = player.getZone();

		int baseObject = action.getInt(ATTR_BASEOBJECT);

		RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
		if (!zone.has(baseobjectid)) {
			return null;
		}

		RPObject base = zone.get(baseobjectid);
		if (!(base instanceof Entity)) {
			// Shouldn't really happen because everything is an entity
			return null;
		}

		Entity baseEntity = (Entity) base;

		if (baseEntity.hasSlot(action.get(ATTR_BASESLOT))) {
			RPSlot slot = baseEntity.getSlot(action.get(ATTR_BASESLOT));

			if (slot.size() == 0) {
				return null;
			}

			RPObject object = null;
			int item = action.getInt(ATTR_BASEITEM);
			// scan through the slot to find the requested item
			for (RPObject rpobject : slot) {
				if (rpobject.getID().getObjectID() == item) {
					object = rpobject;
					break;
				}
			}

			// no item found... we take the first one
			if (object == null) {
				object = slot.iterator().next();
			}

			// It is always an entity
			return (Entity) object;
		}
		
		return null;
	}
}
