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

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * handles the shock status each turn
 *
 * @author Jordan
 */
public class ShockStatusTurnListener implements TurnListener {
	private StatusList statusList;

	/**
	 * ShockStatusTurnListener
	 *
	 * @param statusList StatusList
	 */
	public ShockStatusTurnListener(StatusList statusList) {
		this.statusList = statusList;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		RPEntity entity = statusList.getEntity();
		ShockStatus status = statusList.getFirstStatusByClass(ShockStatus.class);

		// check that the entity exists and has this status
		if ((entity == null) || (status == null)) {
			return;
		}

		// Stop the entity's movement after n steps
		int stepsTaken = entity.getStepsTaken();
		if (stepsTaken >= status.getStepsDelay()) {
			if (entity instanceof Player) {
				((Player) entity).forceStop();
			} else {
				entity.stop();
			}
			entity.clearPath();
		}

		TurnNotifier.get().notifyInTurns(0, this);
	}

}
