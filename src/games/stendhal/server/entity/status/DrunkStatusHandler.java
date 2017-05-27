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

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;

/**
 * handles DrunkStatusHandler
 */
public class DrunkStatusHandler implements StatusHandler<DrunkStatus> {

	/**
	 * inflicts a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 * @param attacker the attacker
	 */
	@Override
	public void inflict(DrunkStatus status, StatusList statusList, Entity attacker) {
		int count = statusList.countStatusByType(status.getStatusType());
		if (count <= 6) {
			statusList.addInternal(status);
		}
		TurnNotifier.get().notifyInSeconds(120, new StatusRemover(statusList, status));
	}

	/**
	 * removes a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	@Override
	public void remove(DrunkStatus status, StatusList statusList) {
		statusList.removeInternal(status);
	}
}
