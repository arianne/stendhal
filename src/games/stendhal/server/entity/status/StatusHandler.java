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

import games.stendhal.server.entity.Entity;


/**
 * handles status changes
 *
 * @author hendrik
 * @param <T> type of status
 */
public interface StatusHandler<T extends Status> {

	/**
	 * inflicts a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 * @param attacker the attacker
	 */
	public void inflict(T status, StatusList statusList, Entity attacker);

	/**
	 * removes a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void remove(T status, StatusList statusList);

}
