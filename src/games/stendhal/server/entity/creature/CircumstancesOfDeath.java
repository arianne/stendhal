/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;

/**
 * Contains statistics for notifications.
 */
public class CircumstancesOfDeath {
	private RPEntity killer;
// 	public List<RPEntity> killers;
	private RPEntity victim;
	private StendhalRPZone zone;

	public RPEntity getKiller() {
		return(killer);
	}

	public RPEntity getVictim() {
		return(victim);
	}

	public StendhalRPZone getZone() {
		return(zone);
	}

	public CircumstancesOfDeath(RPEntity killer, RPEntity victim, StendhalRPZone zone) {
		this.zone=zone;
		this.killer=killer;
		this.victim=victim;
	}
}
