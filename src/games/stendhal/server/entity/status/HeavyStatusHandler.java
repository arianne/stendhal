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
				status.setOriginalSpeed(entity.getBaseSpeed());
				
				/* create random duration between 60 and 90 seconds */
				duration = Rand.randUniform(60, 90);
				
				/* stop entity if it is currently moving */
				if (entity.getSpeed() > 0.0) {
					entity.stop();
				}
				
				/* slow the entity down to half walking speed */
				entity.setBaseSpeed(0.5);
				
				/* restart entity's movement */
				if (entity.getSpeed() == 0) {
					// TODO
				}
				
				/* status was not inflicted by another entity */
				if (attacker == null) {
					entity.sendPrivateText(NotificationType.SCENE_SETTING,
							"Your feet begin to feel heavy. You are weighed down.");
				} else {
					entity.sendPrivateText(NotificationType.SCENE_SETTING,
							"Your feet begin to feel heavy. You have been weighed down by "
					+ attacker.getName() + ".");
				}		
			}
		}
		
		int count = statusList.countStatusByType(status.getStatusType());
		if (count <= 6) {
			statusList.addInternal(status);
		}
		
		if (count == 0) {
			statusList.activateStatusAttribute("status_" + status.getName());
			TurnNotifier.get().notifyInSeconds(duration, new StatusRemover(statusList, status));
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
		final double original_speed = status.getOriginalSpeed();
		statusList.removeInternal(status);

		RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		Status nextStatus = statusList.getFirstStatusByClass(HeavyStatus.class);
		if (nextStatus != null) {
			TurnNotifier.get().notifyInSeconds(duration, new StatusRemover(statusList, nextStatus));
		} else {
			entity.remove("status_" + status.getName());
			/* replace the entity's original speed */
			entity.setBaseSpeed(original_speed);
			entity.sendPrivateText(NotificationType.SCENE_SETTING, "You no longer feel weighed down.");
		}
	}
}
