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

/**
 * handles ShockStatusHandler
 */
public class ShockStatusHandler implements StatusHandler<ShockStatus> {

	/**
	 * inflicts a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void inflict(ShockStatus status, StatusList statusList) {
		if (statusList.hasStatus(status.getStatusType())) {
			return;
		}
		int count = statusList.countStatusByType(status.getStatusType());
		if (count <= 6) {
			statusList.addInternal(status);
		}

		if (count == 0) {
			TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, status));
			TurnNotifier.get().notifyInTurns(1, new ShockStatusTurnListener(statusList));
		}
	}

	/**
	 * removes a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void remove(ShockStatus status, StatusList statusList) {
		statusList.removeInternal(status);
		Status nextStatus = statusList.getFirstStatusByClass(ShockStatus.class);
		if (nextStatus != null) {
			TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, status));
		}
	}
}
