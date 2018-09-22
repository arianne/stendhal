/***************************************************************************
 *                (C) Copyright 2003-2018 - Arianne                        *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.consumption;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.StatusList;
import games.stendhal.server.entity.status.StatusType;

/**
 * Times out status healing/immunizing items.
 *
 * @author hendrik
 * @author AntumDeluge
 */
public class StatusHealerEater implements TurnListener {

	private WeakReference<RPEntity> entityReference;

	private Set<StatusType> statuses = Collections.emptySet();

	/**
	 * Constructor defining one status healed by the item.
	 *
	 * @param entity
	 * 			Entity the item is used on.
	 * @param status
	 * 			Status type this item cures.
	 */
	public StatusHealerEater(final RPEntity entity, final StatusType status) {
		entityReference = new WeakReference<RPEntity>(entity);
		statuses = EnumSet.of(status);
	}

	/**
	 * Constructor defining multiple statuses healed by the item.
	 *
	 * @param entity
	 * 			Entity the item is used on.
	 * @param status
	 * 			List of statuses this item cures.
	 */
	public StatusHealerEater(final RPEntity entity, final Set<StatusType> status) {
		entityReference = new WeakReference<RPEntity>(entity);
		statuses = status;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		RPEntity entity = entityReference.get();
		if (entity == null) {
			return;
		}

		StatusList entityStatuses = entity.getStatusList();
		for (StatusType st: statuses) {
			entityStatuses.removeImmunity(st);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (! (obj instanceof StatusHealerEater)) {
			return false;
		}

		final StatusHealerEater other = (StatusHealerEater) obj;
		RPEntity entity = entityReference.get();
		if (entity == null) {
			return other.entityReference.get() == null;
		}
		return entity.equals(other.entityReference.get());
	}

	@Override
	public int hashCode() {
		RPEntity entity = entityReference.get();
		if (entity == null) {
			return 3798172;
		}
		return entity.hashCode();
	}
}
