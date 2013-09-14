/***************************************************************************
 *                (C) Copyright 2005-2013 - Faiumoni e. V.                 *
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
 * handles eating
 */
public class EatStatusHandler implements StatusHandler<EatStatus> {

	/**
	 * inflicts a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void inflict(EatStatus status, StatusList statusList) {
		int count = statusList.countStatusByType(status.getStatusType());
		statusList.addInternal(status);

		if (count == 0) {
			TurnNotifier.get().notifyInTurns(1, new EatStatusTurnListener(statusList));
		}
	}

	/**
	 * removes a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void remove(EatStatus status, StatusList statusList) {
		statusList.removeInternal(status);
	}
}
