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
package games.stendhal.server.maps.fado.forest;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

/**
 * Creates spawn point for:
 *  slim bottle,
 *  eared bottle
 *
 *  Imorgen's hut, Fado's forest (~50,30)
 *
 *  NOTE:
 *	  slim/eared bottle will spawn among fixed map items
 */

public class ItemsOnTable implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildOutsideHutArea(zone);
	}

	private void buildOutsideHutArea(final StendhalRPZone zone) {

        // grower for a slim bottle (1h)
		final PassiveEntityRespawnPoint bottleGrower1 = new PassiveEntityRespawnPoint("slim bottle", 12000);
		bottleGrower1.setPosition(48, 28);
		bottleGrower1.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower1);
		bottleGrower1.setToFullGrowth();

        // grower for an eared bottle (1h)
		final PassiveEntityRespawnPoint bottleGrower2 = new PassiveEntityRespawnPoint("eared bottle", 12000);
		bottleGrower2.setPosition(49, 31);
		bottleGrower2.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower2);
		bottleGrower2.setToFullGrowth();

	}
}
