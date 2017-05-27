/***************************************************************************
 *                (C) Copyright 2003-2014 - Faiumoni e. V.                 *
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
 * A status ailment that causes the entity to move more slowly
 */
public class HeavyStatus extends Status {

	/**
	 * Create the status
	 */
	public HeavyStatus() {
		super("heavy");
	}

	/**
	 * @return
	 * 		StatusType
	 */
	@Override
	public StatusType getStatusType() {
		return StatusType.HEAVY;
	}
}
