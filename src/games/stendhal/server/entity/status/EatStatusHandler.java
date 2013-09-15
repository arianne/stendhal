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

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;

/**
 * handles eating
 */
public class EatStatusHandler implements StatusHandler<EatStatus> {

	/**
	 * inflicts a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 * @param attacker the attacker
	 */
	@Override
	public void inflict(EatStatus status, StatusList statusList, Entity attacker) {
		int count = statusList.countStatusByType(status.getStatusType());
		statusList.addInternal(status);

		// activate the turnListener, if this is the first instance of this status
		// note: the turnListener is called one last time after the last instance was comsumed to cleanup attributes.
		// So even with count==0, there might still be a listener which needs to be removed
		if (count == 0) {
			TurnListener turnListener = new EatStatusTurnListener(statusList);
			TurnNotifier.get().dontNotify(turnListener);
			TurnNotifier.get().notifyInTurns(0, turnListener);
		}
	}

	/**
	 * removes a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	@Override
	public void remove(EatStatus status, StatusList statusList) {
		statusList.removeInternal(status);
	}
}
