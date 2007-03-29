/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import java.util.List;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.player.Player;

public abstract class ActionListener {

	private static final Logger logger = Log4J.getLogger(ActionListener.class);

	/** callback for the registered action */
	public abstract void onAction(Player player, RPAction action);

	/**
	 * Checks if a specific Attribute is set. returns false when it is not set.
	 * Additionally the logger is informed about the missing attribute
	 * 
	 * @param action
	 *            the action to check
	 * @param name
	 *            name of the attribute
	 * @return true when the attrib is there, false if it is missing
	 */
	protected boolean checkAttribute(RPAction action, String name) {
		if (action.has(name)) {
			// the attrib is there
			return true;
		}

		// attrib is not there
		logger.debug("action attribute " + name + " is missing");
		return false;
	}


	/**
	 * checks if two entities are near each other.
	 * 
	 * @param first
	 *            the first entity
	 * @param second
	 *            the second entity
	 * @param distance
	 *            the valid distance considered 'near'
	 * @return true when the entity is near the player, false otherwise
	 */
	protected boolean checkDistance(Entity first, Entity second, double distance) {
		if (first.nextTo(second, distance)) {
			return true;
		}

		logger.debug("object is to far. distance+ " + distance + " first: " + first.getID() + " second: "
		        + second.getID());
		return false;
	}


}
