/***************************************************************************
 *                 (C) Copyright 2003-2013 - Faiumoni e. V.                *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

/**
 * checks the availability of something
 *
 * @author hendrik
 */
public interface AvailabilityChecker {

	/**
	 * is this resource available?
	 *
	 * @return true, if the resource is available; false otherwise
	 */
	public boolean isAvailable();
}
