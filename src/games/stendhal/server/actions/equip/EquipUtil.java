/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPObject;

/**
 * Useful method to deal with equipable items.
 */
public class EquipUtil {
	public static final Logger logger = Logger.getLogger(EquipUtil.class);

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
		return EntityHelper.entityFromZoneByID(objectId, player.getZone());
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
