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

/**
 * removes a status
 *
 * @author hendrik
 */
public class StatusRemover implements TurnListener {

	private StatusList statusList;
	private Status status;

	/**
	 * StatusRemover
	 *
	 * @param statusList  statusList to remove a status from
	 * @param status      status to remove
	 */
	public StatusRemover(StatusList statusList, Status status) {
		this.statusList = statusList;
		this.status = status;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		statusList.remove(status);
	}
}
