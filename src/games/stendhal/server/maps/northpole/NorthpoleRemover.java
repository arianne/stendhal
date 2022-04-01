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
package games.stendhal.server.maps.northpole;

import java.util.HashSet;
import java.util.Set;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Corpse;
import marauroa.common.game.RPObject;

/**
 * removes particular Northpole
 *
 */
public class NorthpoleRemover implements TurnListener {
	private StendhalRPZone zone;

	/**
	 * creates a new NorthpoleIslandRemover
	 *
	 * @param zone StendhalRPZone
	 */
	public NorthpoleRemover(StendhalRPZone zone) {
		this.zone = zone;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		if (zone.getPlayers().size()==0) {
			// Tell all corpses they are to be removed
			// (stops timers)
			Set<Corpse> corpses = new HashSet<Corpse>();
			for (RPObject object : zone) {
				if (object instanceof Corpse) {
					corpses.add((Corpse) object);
				}
			}
			for (Corpse corpse : corpses) {
				zone.remove(corpse);
			}

			SingletonRepository.getRPWorld().removeZone(zone);
		}
	}

}
