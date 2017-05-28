/***************************************************************************
 *                    (C) Copyright 2013 - Faiumoni e. V.                  *
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
 * A status effect that causes the entity to show signs of being drunk
 *
 * @author hendrik
 */
public class DrunkStatus extends Status {

	/**
	 * Create the status
	 */
	public DrunkStatus() {
		super("drunk");
	}

	/**
	 * returns the status type
	 *
	 * @return StatusType
	 */
	@Override
	public StatusType getStatusType() {
		return StatusType.DRUNK;
	}
}
