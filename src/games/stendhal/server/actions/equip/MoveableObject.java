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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.player.Player;

/**
 * source or destination object.
 *
 * @author hendrik
 */
public abstract class MoveableObject {

	/** optional, parent item .*/
	protected Entity parent;
	/** the slot this item is in or should be placed into. */
	protected String slot;
	/** Player to notify about problems. */
	protected final Player player;

	/**
	 * Creates a new MoveableObject.
	 *
	 * @param player
	 *            Player to notify about problems
	 */

	public MoveableObject(final Player player) {
		this.player = player;
	}

	/**
	 * is this object valid?
	 *
	 * @return true, if the action may be performed, false otherwise
	 */
	public abstract boolean isValid();

	/**
	 * is the owner of the slot in reach?
	 *
	 * @param entity
	 *            entity to compare to
	 * @param distance
	 *            max distance
	 * @return true, if it is reachable, false otherwise
	 */
	public abstract boolean checkDistance(Entity entity, double distance);

	/**
	 * gets the name of the slot or null if there is none.
	 *
	 * @return slot name
	 */
	String getSlot() {
		return slot;
	}

	/**
	 * returns log information.
	 *
	 * @return String[2]
	 */
	public abstract String[] getLogInfo();

	/**
	 * Checks if RPobject is one the valid classes.
	 * @param validClasses
	 * @return true if the rpobject is one of the classes in <i>validClasses</i>.
	 */
	public boolean checkClass(final List<Class< ? >> validClasses) {
		if (parent != null) {
			return EquipUtil.isCorrectClass(validClasses, parent);
		}
		return true;
	}

	/**
	 * Checks if container is a corpse.
	 * @return true if container is a corpse.
	 */
	public boolean isContainerCorpse() {
		if (parent != null) {
			final Class< ? > clazz = Corpse.class;
			return (clazz.isInstance(parent));
		}
		return false;
	}

	boolean isInvalidMoveable(final Player player, final double maxDistance, final List<Class< ? >> containerClassesList) {
		return !isValid() || !checkDistance(player, maxDistance) || (!checkClass(containerClassesList));
	}
}
