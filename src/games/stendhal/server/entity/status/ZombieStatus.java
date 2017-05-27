/***************************************************************************
 *                   (C) Copyright 2003-2015 - Arianne                     *
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
 * A status effect that causes the player to move more slowly
 */
public class ZombieStatus extends Status {

	/**
	 * Create the status
	 */
	public ZombieStatus() {
		super("zombie");
	}

	/**
	 * @return
	 * 		StatusType
	 */
	@Override
	public StatusType getStatusType() {
		return StatusType.ZOMBIE;
	}
}
