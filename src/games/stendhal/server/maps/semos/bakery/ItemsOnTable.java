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
package games.stendhal.server.maps.semos.bakery;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

/**
 * Creates bottles in Semos bakery
 */
public class ItemsOnTable implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildBakeryArea(zone);
	}

	private void buildBakeryArea(final StendhalRPZone zone) {

		// grower for an empty slim bottle that cannot be taken (out of reach)
		final PassiveEntityRespawnPoint bottleGrower1 = new PassiveEntityRespawnPoint("slim bottle", 288000);
		bottleGrower1.setPosition(4, 1);
		bottleGrower1.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower1);

		bottleGrower1.setToFullGrowth();

		// grower for an empty slim bottle (1h)
		final PassiveEntityRespawnPoint bottleGrower2 = new PassiveEntityRespawnPoint("slim bottle", 12000);
		bottleGrower2.setPosition(22, 2);
		bottleGrower2.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower2);

		bottleGrower2.setToFullGrowth();

	}
}
