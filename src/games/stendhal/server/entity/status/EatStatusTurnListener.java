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

import java.util.Collections;
import java.util.List;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * eating turn listener
 */
public class EatStatusTurnListener implements TurnListener {
	private StatusList statusList;
	private static final String ATTRIBUTE_NAME = "eating";
	private static final String ATTRIBUTE_NAME_CHOKING = "choking";

	/**
	 * EatStatusTurnListener
	 *
	 * @param statusList StatusList
	 */
	public EatStatusTurnListener(StatusList statusList) {
		this.statusList = statusList;
	}

	@Override
	public void onTurnReached(int turn) {
		RPEntity entity = statusList.getEntity();
		List<EatStatus> toConsume = statusList.getAllStatusByClass(EatStatus.class);

		// check that the entity exists
		if (entity == null) {
			return;
		}

		// cleanup status
		if (toConsume.isEmpty()) {
			if (entity.has(ATTRIBUTE_NAME)) {
				entity.remove(ATTRIBUTE_NAME);
			}
			if (entity.has(ATTRIBUTE_NAME_CHOKING)) {
				entity.remove(ATTRIBUTE_NAME_CHOKING);
			}
			entity.notifyWorldAboutChanges();
			return;
		}

		Collections.sort(toConsume);
		final ConsumableStatus food = toConsume.get(0);

		if (turn % food.getFrecuency() == 0) {
			final int amount = food.consume();
			if (isChoking(toConsume)) {
				entity.put(ATTRIBUTE_NAME_CHOKING, amount);
			} else {
				if (entity.has(ATTRIBUTE_NAME_CHOKING)) {
					entity.remove(ATTRIBUTE_NAME_CHOKING);
				}
				entity.put(ATTRIBUTE_NAME, amount);
				entity.notifyWorldAboutChanges();
			}

			// is full hp?
			if (entity.heal(amount, true) == 0) {
				if (entity instanceof Player) {
					statusList.removeAll(EatStatus.class);
				}
			}

			// is item used up?
			if (food.consumed()) {
				statusList.remove(food);
			}
		}

		TurnNotifier.get().notifyInTurns(0, this);
	}

	private boolean isChoking(List<EatStatus> toConsume) {
		return toConsume.size() > 5;
	}

	@Override
	public int hashCode() {
		return statusList.hashCode() * 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		EatStatusTurnListener other = (EatStatusTurnListener) obj;
		return statusList.equals(other.statusList);
	}

}
