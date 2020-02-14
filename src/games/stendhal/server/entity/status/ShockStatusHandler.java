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
import games.stendhal.common.Rand;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

/**
 * handles ShockStatusHandler
 */
public class ShockStatusHandler implements StatusHandler<ShockStatus> {

	private StatusRemover remover;

	/**
	 * inflicts a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 * @param attacker the attacker
	 */
	@Override
	public void inflict(ShockStatus status, StatusList statusList, Entity attacker) {

		final String resistName = "resist_shocked";

		if (!statusList.hasStatus(status.getStatusType())) {
			RPEntity entity = statusList.getEntity();
			if (entity != null) {
				if (attacker == null) {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are shocked.");
				} else {
					entity.sendPrivateText(NotificationType.SCENE_SETTING, "You have been shocked by " + attacker.getName() + ".");
				}
				statusList.addInternal(status);
				statusList.activateStatusAttribute("status_" + status.getName());

				remover = new StatusRemover(statusList, status);

				// lasts between 30 seconds & 5 minutes
				int persistence = Rand.randUniform(30, 60 * 5);
				// shock-resistance also alters duration
				if (entity.has(resistName)) {
					persistence = (int) Math.round(persistence * (1.0 - entity.getDouble(resistName)));
				}

				TurnNotifier.get().notifyInSeconds(persistence, remover);
				TurnNotifier.get().notifyInTurns(0, new ShockStatusTurnListener(statusList));
			}
		}
	}

	/**
	 * removes a status
	 *
	 * @param status Status to inflict
	 * @param statusList StatusList
	 */
	@Override
	public void remove(ShockStatus status, StatusList statusList) {
		statusList.removeInternal(status);

		final RPEntity entity = statusList.getEntity();
		if (entity == null) {
			return;
		}

		entity.sendPrivateText(NotificationType.SCENE_SETTING, "You are no longer shocked.");
		entity.remove("status_" + status.getName());

		// disable pending notifications
		if (remover != null) {
			TurnNotifier.get().dontNotify(remover);
			remover = null;
		}
	}
}
