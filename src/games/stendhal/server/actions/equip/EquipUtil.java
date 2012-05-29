/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.equip;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Useful method to deal with equipable items.
 */
public class EquipUtil {
	private static Logger logger = Logger.getLogger(EquipUtil.class);

	/**
	 * The maximum distance a player can throw an item away from himself.
	 */
	protected static final int MAX_THROWING_DISTANCE = 8;

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
	public static Entity getEntityFromId(final Player player, final int objectId) {
		final StendhalRPZone zone = player.getZone();
		final RPObject.ID id = new RPObject.ID(objectId, zone.getID());

		if (!zone.has(id)) {
			logger.debug("Rejected because zone doesn't have object "
					+ objectId);
			return null;
		}

		return (Entity) zone.get(id);
	}
	
	/**
	 * Get an entity from path. Does not do any access checks.
	 * 
	 * @param player
	 * @param path entity path
	 * @return entity corresponding to the path, or <code>null</code> if none
	 * 	was found
	 */
	static Entity getEntityFromPath(final Player player, final List<String> path) {
		Iterator<String> it = path.iterator();
		// The ultimate parent object
		Entity parent = getEntityFromId(player, MathHelper.parseInt(it.next()));
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
				logger.error(player.getName() + " tried to use non existing slot " + slotName + " of " + entity
						+ " as source. player zone: " + player.getZone() + " object zone: " + parent.getZone());
				return null;
			}
			
			final RPSlot slot = entity.getSlot(slotName);
			
			if (!it.hasNext()) {
				logger.error("Missing entity id");
				return null;
			}
			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				logger.debug("Base entity(" + entity + ") doesn't contain entity(" + itemId + ") on given slot(" + slotName
						+ ")");
				return null;
			}
			
			entity = (Entity) slot.get(itemId);
		}

		return entity;
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
	static boolean isCorrectClass(final List<Class< ? >> validClasses, final RPObject object) {
		for (final Class< ? > clazz : validClasses) {
			if (clazz.isInstance(object)) {
				return true;
			}
		}
		logger.debug("object " + object.getID()
				+ " is not of the correct class. it is "
				+ object.getClass().getName());
		return false;
	}
}
