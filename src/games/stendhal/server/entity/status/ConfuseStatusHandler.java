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
import games.stendhal.server.entity.Entity;
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
	 * @param attacker the attacker
	 */
	@Override
	public void inflict(ConfuseStatus status, StatusList statusList, Entity attacker) {
		if (statusList.hasStatus(status.getStatusType())) {
			return;
		}

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}
		if (attacker == null) {
			entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are confused.");
		} else {
			entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been confused by " + attacker.getName() + ".");
		}

		statusList.activateStatusAttribute("status_" + status.getName());
		statusList.addInternal(status);
		TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, status));
	}

	/**
	 * removes a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	@Override
	public void remove(ConfuseStatus status, StatusList statusList) {
		statusList.removeInternal(status);

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are no longer confused.");
		entity.remove("status_" + status.getName());
	}
}
