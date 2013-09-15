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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;

/**
 * handles ConfuseStatus
 */
public class ConfuseStatusHandler implements StatusHandler<ConfuseStatus> {

	/**
	 * inflicts a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void inflict(ConfuseStatus status, StatusList statusList) {
		if (statusList.hasStatus(status.getStatusType())) {
			return;
		}

		statusList.addInternal(status);
		TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, status));
	}

	/**
	 * removes a status
	 * 
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	public void remove(ConfuseStatus status, StatusList statusList) {
		statusList.removeInternal(status);

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		entity.sendPrivateText(NotificationType.SCENE_SETTING, "\"" + status.getStatusType().getName() + "\" has worn off.");
		entity.remove("status_" + status.getStatusType().getName());
	}
}
