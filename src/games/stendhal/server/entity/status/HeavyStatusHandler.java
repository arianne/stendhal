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

import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

/**
 * Handles HeavyStatus
 *
 */
public class HeavyStatusHandler implements StatusHandler<HeavyStatus> {

	private int duration;

	/** The original speed of the entity */
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
	public void inflict(HeavyStatus status, StatusList statusList, Entity attacker) {

		if (!statusList.hasStatus(status.getStatusType())) {
			RPEntity entity = statusList.getEntity();
			if (entity != null) {
				/* save the entity's original speed to be replaced later */
				originalSpeed = entity.getBaseSpeed();
				entity.setBaseSpeed(0.5);

				/* slow the entity down to half walking speed */
				entity.setBaseSpeed(entity.getBaseSpeed() / 2);

				/* create random duration between 30 seconds and 5 minutes */
				Double d_min = 3.3 * 30;
				Double d_max = 3.3 * 300;
				duration = Rand.randUniform(d_min.intValue(), d_max.intValue());

				/* status was not inflicted by another entity */
				if (attacker == null) {
					entity.sendPrivateText(NotificationType.SCENE_SETTING,
							"Your feet begin to feel heavy. You are weighed down.");
				} else {
					entity.sendPrivateText(NotificationType.SCENE_SETTING,
							"Your feet begin to feel heavy. You have been weighed down by "
					+ attacker.getName() + ".");
				}

				statusList.addInternal(status);
				statusList.activateStatusAttribute("status_" + status.getName());
				TurnNotifier.get().notifyInSeconds(duration, new StatusRemover(statusList, status));
			}
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
	public void remove(HeavyStatus status, StatusList statusList) {
		statusList.removeInternal(status);

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		Status nextStatus = statusList.getFirstStatusByClass(HeavyStatus.class);
		/* replace the entity's original speed */
		entity.setBaseSpeed(originalSpeed);
		if (nextStatus != null) {
			TurnNotifier.get().notifyInSeconds(duration, new StatusRemover(statusList, nextStatus));
		} else {
			entity.remove("status_" + status.getName());
			entity.sendPrivateText(NotificationType.SCENE_SETTING, "You no longer feel weighed down.");
		}
	}
}
