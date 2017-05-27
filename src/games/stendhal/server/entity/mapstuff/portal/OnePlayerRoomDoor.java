/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;

/**
 * A door to a zone which only one player may enter.
 *
 * @author hendrik
 */
public class OnePlayerRoomDoor extends Door {

	/**
	 * Tries periodically to open the door. (Just in case the player left zone
	 * event did not get fired).
	 */
	class PeriodicOpener implements TurnListener {

		@Override
		public void onTurnReached(final int currentTurn) {
			if (!isOpen()) {
				if (isAllowed(null)) {
					open();
				}
			}
			SingletonRepository.getTurnNotifier().notifyInTurns(60, this);
		}

	}

	/**
	 * Creates a new OnePlayerRoomDoor.
	 *
	 * @param clazz
	 *            clazz
	 */
	public OnePlayerRoomDoor(final String clazz) {
		super(clazz);
		SingletonRepository.getTurnNotifier().notifyInTurns(60, new PeriodicOpener());
	}

	@Override
	protected boolean isAllowed(final RPEntity user) {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = world.getZone(super.getDestinationZone());
		return (zone.getPlayers().size() == 0);
	}
}
