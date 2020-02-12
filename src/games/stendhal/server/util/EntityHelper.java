/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import static games.stendhal.common.constants.Actions.TARGET;

import java.util.Iterator;
import java.util.List;

import games.stendhal.common.Constants;
import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.equip.EquipUtil;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.GroundSlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Utilities to handle entities in the server.
 *
 * @author Martin Fuchs
 */
public class EntityHelper {
	private static final String ATTR_BASESLOT = "baseslot";
	private static final String ATTR_BASEOBJECT = "baseobject";
	private static final String ATTR_BASEITEM = "baseitem";

	/**
	 * Returns an entity reference by an objectId in a zone.
	 *
	 * @param objectId objectId of this Entity
	 * @param zone zone
	 * @return Entity or <code>null</code>
	 */
	public static Entity entityFromZoneByID(final int objectId, final StendhalRPZone zone) {
		final RPObject.ID targetid = new RPObject.ID(objectId, zone.getID());
		if (zone.has(targetid)) {
			final RPObject object = zone.get(targetid);
			if (object instanceof Entity) {
				return (Entity) object;
			}
		}
		return null;
	}

	/**
	 * Translate the "target" parameter of actions like "look" into an entity
	 * reference. Numeric parameters are treated as object IDs, alphanumeric
	 * names are searched in the list of players and NPCs.
	 *
	 * @param target
	 *			  representation of the target
	 * @param player
	 *			  to constraint for current zone and screen area
	 * @return the entity associated either with name or id or
	 *		   <code> null </code> if none was found or any of
	 *		   the input parameters was <code> null </code>.
	 */
	public static Entity entityFromTargetName(final String target, final Entity player) {
		if ((target == null) || (player == null)) {
			return null;
		}

		final StendhalRPZone zone = player.getZone();
		Entity entity = null;

		if ((target.length() > 1) && (target.charAt(0) == '#')
				&& Character.isDigit(target.charAt(1))) {
			final int objectId = Integer.parseInt(target.substring(1));

			entity = entityFromZoneByID(objectId, zone);
		}

		if (entity == null) {
			entity = SingletonRepository.getRuleProcessor().getPlayer(target);

			if ((entity != null) && !player.isInSight(entity)) {
				entity = null;
			}
		}

		if (entity == null) {
			entity = SingletonRepository.getNPCList().get(target);

			if ((entity != null) && !player.isInSight(entity)) {
				entity = null;
			}
		}

		return entity;
	}

	/**
	 * Translate the "target" parameter of actions like "look" into an entity
	 * reference. Numeric parameters are treated as object IDs, alphanumeric
	 * names are searched in the list of players and NPCs.
	 *
	 * @param target
	 *			  representation of the target
	 * @param player
	 *			  to constraint for current zone and screen area
	 * @return the entity associated either with name or id or
	 *		   <code> null </code> if none was found or any of
	 *		   the input parameters was <code> null </code>.
	 */
	public static Entity entityFromTargetNameAnyZone(final String target, final Entity player) {
		if ((target == null) || (player == null)) {
			return null;
		}

		final StendhalRPZone zone = player.getZone();
		Entity entity = null;

		if ((target.length() > 1) && (target.charAt(0) == '#')
				&& Character.isDigit(target.charAt(1))) {
			final int objectId = Integer.parseInt(target.substring(1));

			entity = entityFromZoneByID(objectId, zone);
		}

		if (entity == null) {
			entity = SingletonRepository.getRuleProcessor().getPlayer(target);


		}

		if (entity == null) {
			entity = SingletonRepository.getNPCList().get(target);
		}

		return entity;
	}

	/**
	 * Retrieves a specified item from a slot. Necessary attributes in the RPAction:
	 * 	- baseslot name of the slot to search in
	 *  - baseobject the id of the object where to search for the specified slot
	 *  - baseitem the id of the object to search for
	 *
	 * @param player the player where to search for the item
	 * @param action the action specifying for what to search
	 * @return the found Entity or null
	 */
	public static Entity entityFromSlot(final Player player, final RPAction action) {
		// entity in a slot?
		if (!action.has(ATTR_BASEITEM)
				|| !action.has(ATTR_BASEOBJECT)
				|| !action.has(ATTR_BASESLOT)) {
			return null;
		}

		final StendhalRPZone zone = player.getZone();

		final int baseObject = action.getInt(ATTR_BASEOBJECT);

		final RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
		if (!zone.has(baseobjectid)) {
			return null;
		}

		final RPObject base = zone.get(baseobjectid);
		if (!(base instanceof Entity)) {
			// Shouldn't really happen because everything is an entity
			return null;
		}

		final Entity baseEntity = (Entity) base;

		if (baseEntity.hasSlot(action.get(ATTR_BASESLOT))) {
			final RPSlot slot = baseEntity.getSlot(action.get(ATTR_BASESLOT));

			if (slot.size() == 0) {
				return null;
			}

			RPObject object = null;
			final int item = action.getInt(ATTR_BASEITEM);
			// scan through the slot to find the requested item
			for (final RPObject rpobject : slot) {
				if (rpobject.getID().getObjectID() == item) {
					object = rpobject;
					break;
				}
			}

			// It is always an entity
			return (Entity) object;
		}

		return null;
	}

	/**
	 * gets the item slot from an action
	 *
	 * @param player Player executing the action
	 * @param action action
	 * @return EntitySlot or <code>null</code> if the action was invalid
	 */
    public static EntitySlot getSlot(Player player, RPAction action) {
        final StendhalRPZone zone = player.getZone();

        if (action.has(ATTR_BASEITEM) && action.has(ATTR_BASEOBJECT) && action.has(ATTR_BASESLOT)) {

            final int baseObject = action.getInt(ATTR_BASEOBJECT);

            final RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
            if (!zone.has(baseobjectid)) {
                return null;
            }

            final RPObject base = zone.get(baseobjectid);
            if (!(base instanceof Entity)) {
                // Shouldn't really happen because everything is an entity
                return null;
            }

            final Entity baseEntity = (Entity) base;

            if (baseEntity.hasSlot(action.get(ATTR_BASESLOT))) {
                final RPSlot slot = baseEntity.getSlot(action.get(ATTR_BASESLOT));
                if (!(slot instanceof EntitySlot)) {
                    return null;
                }
                return (EntitySlot) slot;
            }

        } else if (action.has(TARGET)) {

            String target = action.get(TARGET);

            if ((target.length() > 1) && (target.charAt(0) == '#')
                    && Character.isDigit(target.charAt(1))) {
                final int objectId = Integer.parseInt(target.substring(1));

                Entity entity = entityFromZoneByID(objectId, zone);
                if (entity instanceof Item) {
                    return new GroundSlot(zone, entity);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

	/**
	 * Get an entity from path. Does not do any access checks.
	 *
	 * @param player
	 * @param path entity path
	 * @return entity corresponding to the path, or <code>null</code> if none
	 * 	was found
	 */
	public static Entity getEntityFromPath(final Player player, final List<String> path) {
		Iterator<String> it = path.iterator();
		// The ultimate parent object
		Entity parent = entityFromZoneByID(MathHelper.parseInt(it.next()), player.getZone());
		if (parent == null) {
			return null;
		}

		// Walk the slot path
		Entity entity = parent;
		String slotName = null;
		while (it.hasNext()) {
			slotName = it.next();
			if (!entity.hasSlot(slotName)) {
				player.sendPrivateText("Source " + slotName + " does not exist");
				EquipUtil.logger.error(player.getName() + " tried to use non existing slot " + slotName + " of " + entity
						+ " as source. player zone: " + player.getZone() + " object zone: " + parent.getZone());
				return null;
			}

			final RPSlot slot = entity.getSlot(slotName);

			if (!it.hasNext()) {
				EquipUtil.logger.error("Missing entity id");
				return null;
			}
			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				EquipUtil.logger.debug("Base entity(" + entity + ") doesn't contain entity(" + itemId + ") on given slot(" + slotName
						+ ")");
				return null;
			}

			entity = (Entity) slot.get(itemId);
		}

		return entity;
	}

	private static Entity getEntityFromSlotByName(final Player player, final String slotName, final String itemName) {
		RPSlot slot = player.getSlot(slotName);
		if (slot == null) {
			return null;
		}
		for (final RPObject item : slot) {
			if (item.get("name").equals(itemName)) {
				return (Entity) item;
			}
		}

		return null;
	}

	/**
	 * gets an entity by name from the players bag
	 * @param player Player
	 * @param itemName entity name
	 * @return Entity or <code>null</code>
	 */
	public static Entity getEntityByName(final Player player, final String itemName) {
		if (itemName == null) {
			return null;
		}
		final String singularItemName = Grammar.singular(itemName);

		for (final String slotName : Constants.CARRYING_SLOTS) {
			Entity entity = getEntityFromSlotByName(player, slotName, itemName);
			if (entity != null) {
				return entity;
			}

			if (!itemName.equals(singularItemName)) {
				entity = getEntityFromSlotByName(player, slotName, singularItemName);
				if (entity != null) {
					return entity;
				}
			}
		}
		return null;
	}
}
