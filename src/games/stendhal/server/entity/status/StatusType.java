/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

/**
 * types of statuses
 *
 * @author hendrik
 */
public enum StatusType {

	/** cannot walk streight */
	CONFUSED,

	/** is consuming food */
	EATING,

	/** is consuming poison */
	POISONED,

	/** cannot move */
	SHOCKED;

	/**
	 * gets the name of the status type
	 *
	 * @return name
	 */
	public String getName() {
		return this.name().toLowerCase();
	}
}
