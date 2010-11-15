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

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

import java.util.Map;

/**
 * Creates a slim and an eared bottle on the counter in front of the small hut.
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildOutsideHutArea(zone);
	}

	private void buildOutsideHutArea(final StendhalRPZone zone) {

		final PassiveEntityRespawnPoint bottleGrower1 = new PassiveEntityRespawnPoint("slim bottle", 100);
		bottleGrower1.setPosition(48, 28);
		bottleGrower1.setDescription("A spot where a bottle could be standing.");
		zone.add(bottleGrower1);

		bottleGrower1.setToFullGrowth();

		final PassiveEntityRespawnPoint bottleGrower2 = new PassiveEntityRespawnPoint("eared bottle", 100);
		bottleGrower2.setPosition(49, 31);
		bottleGrower2.setDescription("A spot where a bottle could be standing.");
		zone.add(bottleGrower2);

		bottleGrower2.setToFullGrowth();

	}
}
