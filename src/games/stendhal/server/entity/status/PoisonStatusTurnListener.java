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
	private static final String ATTRIBUTE_NAME = "poisoned";

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
		List<PoisonStatus> toConsume = statusList.getAllStatusByClass(PoisonStatus.class);

		// check that the entity exists
		if (entity == null) {
			return;
		}

		// cleanup poison status
		if (toConsume.isEmpty()) {
			if (entity.has(ATTRIBUTE_NAME)) {
				entity.remove(ATTRIBUTE_NAME);
			}
			return;
		}

		List<PoisonStatus> toRemove = new LinkedList<PoisonStatus>();
		int sum = 0;
		int amount = 0;
		for (final PoisonStatus poison : toConsume) {
			if (turn % poison.getFrecuency() == 0) {
				if (poison.consumed()) {
					toRemove.add(poison);
				} else {
					amount = poison.consume();
					entity.damage(-amount, poison);
					sum += amount;
					entity.put(ATTRIBUTE_NAME, sum);
				}
				entity.notifyWorldAboutChanges();
			}
		}

		for (final PoisonStatus poison : toRemove) {
			statusList.remove(poison);
		}
		TurnNotifier.get().notifyInTurns(1, this);
	}

}
