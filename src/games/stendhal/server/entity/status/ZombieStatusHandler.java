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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

/**
 * Handles ZombieStatus
 *
 */
public class ZombieStatusHandler implements StatusHandler<ZombieStatus> {

	/** The original base speed of the entity before being zombified */
	private double originalSpeed;

	/**
	 * @param status
	 * 		Status to inflict
	 * @param statusList
	 * 		StatusList
	 * @param attacker
	 * 		the attacker
	 */
	@Override
	public void inflict(ZombieStatus status, StatusList statusList, Entity attacker) {

		if (!statusList.hasStatus(status.getStatusType())) {
			RPEntity entity = statusList.getEntity();
			if (entity != null) {
				/* save the entity's original speed to be replaced later */
				originalSpeed = entity.getBaseSpeed();
				entity.setBaseSpeed(0.5);
				if (attacker == null) {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are zombified.");
				} else {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been zombified by " + attacker.getName() + ".");
				}
			}

			statusList.addInternal(status);

			statusList.activateStatusAttribute("status_" + status.getName());
			TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, status));
		}

	}

	/**
	 * removes a status
	 *
	 * @param status
	 * 		inflicted status
	 * @param statusList
	 * 		StatusList
	 */
	@Override
	public void remove(ZombieStatus status, StatusList statusList) {
		statusList.removeInternal(status);

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		Status nextStatus = statusList.getFirstStatusByClass(ZombieStatus.class);
		/* replace the entity's original speed */
		entity.setBaseSpeed(originalSpeed);
		if (nextStatus != null) {
			TurnNotifier.get().notifyInSeconds(60, new StatusRemover(statusList, nextStatus));
		} else {
			entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are no longer zombified.");
			entity.remove("status_" + status.getName());
		}
	}
}
