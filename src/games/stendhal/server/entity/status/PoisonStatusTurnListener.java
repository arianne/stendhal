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
import games.stendhal.server.entity.RPEntity;

import java.util.LinkedList;
import java.util.List;

/**
 * poison turn listener
 */
public class PoisonStatusTurnListener implements TurnListener {
	private StatusList statusList;

	/**
	 * PoisonStatusTurnListener
	 * 
	 * @param statusList StatusList
	 */
	public PoisonStatusTurnListener(StatusList statusList) {
		this.statusList = statusList;
	}

	public void onTurnReached(int turn) {
		RPEntity entity = statusList.getEntity();
		List<PoisonStatus> poisonToConsume = statusList.getAllStatusByClass(PoisonStatus.class);

		// check that the entity exists
		if (entity == null) {
			return;
		}

		// cleanup poison status
		if (poisonToConsume.isEmpty()) {
			if (entity.has("poisoned")) {
				entity.remove("poisoned");
			}
			return;
		}

		List<PoisonStatus> poisonsToRemove = new LinkedList<PoisonStatus>();
		int sum = 0;
		int amount = 0;
		for (final PoisonStatus poison : poisonToConsume) {
			if (turn % poison.getFrecuency() == 0) {
				if (poison.consumed()) {
					poisonsToRemove.add(poison);
				} else {
					amount = poison.consume();
					entity.damage(-amount, poison);
					sum += amount;
					entity.put("poisoned", sum);
				}
				entity.notifyWorldAboutChanges();
			}
		}

		for (final PoisonStatus poison : poisonsToRemove) {
			statusList.remove(poison);
		}
		TurnNotifier.get().notifyInTurns(1, this);
	}

}
